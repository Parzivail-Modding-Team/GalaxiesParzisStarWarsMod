(function() {

var import_action, export_action;

Plugin.register('export_nemi', {
	title: 'NEMi Model Format',
	icon: 'star',
	author: 'parzivail',
	description: 'Enables exporting NBT Entity Model Intermediary (NEMi) models, to be compiled into NEMs',
	about: '',
	tags: ["parzi-modding-team"],
	version: '1.0.0',
	min_version: '3.8.0',
	variant: 'both',
	onload() {
		var codec = new Codec('nemi', {
			name: 'NEMi',
			extension: 'nemi',
			remember: false,
			compile(options) {

				let all_groups = getAllGroups();
				let loose_cubes = [];
				Cube.all.forEach(cube => {
					if (cube.parent == 'root') loose_cubes.push(cube)
				})
				if (loose_cubes.length) {
					let group = new Group({
						name: 'bb_main'
					});
					group.is_catch_bone = true;
					group.createUniqueName()
					all_groups.push(group)
					group.children.replace(loose_cubes)
				}

				all_groups.slice().forEach(group => {
					let subgroups = [];
					let group_i = all_groups.indexOf(group);
					group.children.forEachReverse(cube => {
						if (cube instanceof Cube == false || !cube.export) return;
						if (!cube.rotation.allEqual(0)) {
							let sub = subgroups.find(s => {
								if (!s.rotation.equals(cube.rotation)) return false;
								if (s.rotation.filter(n => n).length > 1) {
									return s.origin.equals(cube.origin)
								} else {
									for (var i = 0; i < 3; i++) {
										if (s.rotation[i] == 0 && s.origin[i] != cube.origin[i]) return false;
									}
									return true;
								}
							})
							if (!sub) {
								sub = new Group({
									rotation: cube.rotation,
									origin: cube.origin,
									name: `${cube.name}_r1`
								})
								sub.parent = group;
								sub.is_rotation_subgroup = true;
								sub.createUniqueName(all_groups)
								subgroups.push(sub)
								group_i++;
								all_groups.splice(group_i, 0, sub);
							}
							sub.children.push(cube);
						}
					})
				})

				let modelData = {
					"exporter": "Blockbench " + Blockbench.version,
					"name": Project.geometry_name || "",
					"tex": {
						"w": Project.texture_width,
						"h": Project.texture_height
					},
					"parts": {
					}
				};

				for (var group of all_groups) {

					if ((group instanceof Group === false && !group.is_catch_bone) || !group.export) continue;
					let groupData = {
						"parent": group.parent.name,
						"rot": {
							"pitch": -group.rotation[0],
							"yaw": -group.rotation[1],
							"roll": group.rotation[2]
						},
						"boxes": []
					};

					var origin = group.origin.slice();
					if (group.parent instanceof Group) {
						origin.V3_subtract(group.parent.origin)
					}

					origin[0] *= -1;
					origin[1] *= -1;

					if (group.parent instanceof Group === false) {
						origin[1] += 24
					}

					groupData.pos = {
						"x": origin[0],
						"y": origin[1],
						"z": origin[2],
					}

					for (var cube of group.children) {
						if (cube instanceof Cube === false || !cube.export || (!cube.rotation.allEqual(0) && !group.is_rotation_subgroup)) continue;

						let cubeData = {
							"tex": {
								"u": cube.uv_offset[0],
								"v": cube.uv_offset[1]
							},
							"pos": {
								"x": group.origin[0] - cube.to[0],
								"y": -cube.from[1] - cube.size(1) + group.origin[1],
								"z": cube.from[2] - group.origin[2]
							},
							"size": {
								"x": cube.size(0, true),
								"y": cube.size(1, true),
								"z": cube.size(2, true)
							},
							"inflate": cube.inflate,
							"mirrored": cube.mirror_uv
						};

						groupData.boxes.push(cubeData);
					}
						
					modelData.parts[group.name] = groupData;
				}

				return JSON.stringify(modelData);
			}
		}),

		export_action = new Action('export_nemi', {
			name: 'Export NEMi',
			description: '',
			icon: 'star',
			category: 'file',
			click() {
				codec.export();
			}
		})

		MenuBar.addAction(export_action, 'file.export')

	}
});

})()