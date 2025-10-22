function getMtlFace(obj, index) {
	//if (index % 2 == 1) index--;
	var key = Canvas.face_order[index];
	var tex = obj.faces[key].getTexture();

	if (tex === null) {
		return undefined;
	} else if (!tex || typeof tex === "string") {
		return null;
	} else {
		return tex.id;
	}
}
const cube_face_normals = {
	north: [0, 0, -1],
	east: [1, 0, 0],
	south: [0, 0, 1],
	west: [-1, 0, 0],
	up: [0, 1, 0],
	down: [0, -1, 0],
};

Plugin.register("pswg_ops", {
	title: "PSWG Ops",
	author: "parzivail",
	icon: "icon-format_java",
	description: "File and model operations to support Galaxies: Parzi's Star Wars Mod",
	version: "1.0.0",
	variant: "both",
	onload() {
		var codec = new Codec("pswg_model", {
			name: "PSWG JSON Model",
			remember: true,
			extension: "json",
			support_partial_export: true,
			compile(options) {
				if (options === undefined) options = {};

				var old_scene_position = new THREE.Vector3().copy(scene.position);
				scene.position.set(0, 0, 0);

				/**
				Based on: three.js obj exporter, MIT license
				https://github.com/mrdoob/three.js/blob/dev/examples/misc_exporter_obj.html
				*/

				let materials = {};

				let output = {
					vertices: [],
					normals: [],
					texCoords: [],
					faces: {},
				};

				let currentObjectName = "root";
				let currentMaterialName = "default";

				function pushFace(triplets) {
					output.faces[currentObjectName] = output.faces[currentObjectName] || [];
					output.faces[currentObjectName].push({
						material: currentMaterialName,
						triplets: triplets,
					});
				}

				let indexVertex = 0;
				let indexVertexUvs = 0;
				let indexNormals = 0;
				const vertex = new THREE.Vector3();
				const normal = new THREE.Vector3();
				const uv = new THREE.Vector2();
				const face = [];
				let face_export_mode = Settings.get("obj_face_export_mode");
				let export_scale = Settings.get("model_export_scale");

				var parseMesh = function (mesh) {
					var nbVertex = 0;
					var nbVertexUvs = 0;
					var nbNormals = 0;

					var geometry = mesh.geometry;
					var element = OutlinerNode.uuids[mesh.name];
					const normalMatrixWorld = new THREE.Matrix3();

					if (!element) return;
					if (element.export === false) return;

					normalMatrixWorld.getNormalMatrix(mesh.matrixWorld);

					if (element instanceof Cube) {
						currentObjectName = element.name || "root";

						element.getGlobalVertexPositions().forEach((coords) => {
							vertex.set(...coords).divideScalar(export_scale);
							output.vertices.push([vertex.x, vertex.y, vertex.z]);
							nbVertex++;
						});

						for (let key in element.faces) {
							if (element.faces[key].texture !== null) {
								let face = element.faces[key];
								let texture = face.getTexture();
								let uv_size = [Project.getUVWidth(texture), Project.getUVHeight(texture)];
								let uv_outputs = [];
								uv_outputs.push([face.uv[0] / uv_size[0], 1 - face.uv[1] / uv_size[1]]);
								uv_outputs.push([face.uv[2] / uv_size[0], 1 - face.uv[1] / uv_size[1]]);
								uv_outputs.push([face.uv[2] / uv_size[0], 1 - face.uv[3] / uv_size[1]]);
								uv_outputs.push([face.uv[0] / uv_size[0], 1 - face.uv[3] / uv_size[1]]);
								var rot = face.rotation || 0;
								while (rot > 0) {
									uv_outputs.splice(0, 0, uv_outputs.pop());
									rot -= 90;
								}
								output.texCoords.push(...uv_outputs);
								nbVertexUvs += 4;
							}
						}
						for (let key in element.faces) {
							if (element.faces[key].texture !== null) {
								normal.fromArray(cube_face_normals[key]);
								normal.applyMatrix3(normalMatrixWorld).normalize();
								output.normals.push([normal.x, normal.y, normal.z]);
								nbNormals += 1;
							}
						}

						let mtl;
						let i = 0;
						for (let key in element.faces) {
							if (element.faces[key].texture !== null) {
								let tex = element.faces[key].getTexture();
								if (tex && tex.id && !materials[tex.id]) {
									materials[tex.id] = tex;
								}
								let mtl_new = !tex || typeof tex === "string" ? null : tex.id;
								if (mtl_new != mtl) {
									mtl = mtl_new;
									currentMaterialName = mtl;
								}
								let vertices;
								switch (key) {
									case "north":
										vertices = [2, 5, 7, 4];
										break;
									case "east":
										vertices = [1, 2, 4, 3];
										break;
									case "south":
										vertices = [6, 1, 3, 8];
										break;
									case "west":
										vertices = [5, 6, 8, 7];
										break;
									case "up":
										vertices = [5, 2, 1, 6];
										break;
									case "down":
										vertices = [8, 3, 4, 7];
										break;
								}

								pushFace([
									[vertices[3] + indexVertex, i * 4 + 4 + indexVertexUvs, i + 1 + indexNormals],
									[vertices[2] + indexVertex, i * 4 + 3 + indexVertexUvs, i + 1 + indexNormals],
									[vertices[1] + indexVertex, i * 4 + 2 + indexVertexUvs, i + 1 + indexNormals],
									[vertices[0] + indexVertex, i * 4 + 1 + indexVertexUvs, i + 1 + indexNormals],
								]);

								i++;
							}
						}
					} else if (element instanceof Mesh) {
						currentObjectName = element.name || "mesh";

						let smooth_vertex_normals = element.calculateNormals();
						let vertex_keys = [];
						let vertexnormals = [];
						function addVertex(vkey, x, y, z) {
							vertex.set(x, y, z);
							vertex.applyMatrix4(mesh.matrixWorld).divideScalar(export_scale);
							output.vertices.push([vertex.x, vertex.y, vertex.z]);
							nbVertex++;
							if (element.shading == "smooth") {
								normal.fromArray(smooth_vertex_normals[vkey]);
								normal.applyMatrix3(normalMatrixWorld).normalize();
								vertexnormals.push([normal.x, normal.y, normal.z]);
								nbNormals += 1;
							}
						}
						for (let vkey in element.vertices) {
							addVertex(vkey, ...element.vertices[vkey]);
							vertex_keys.push(vkey);
						}

						let mtl;
						let i = 0;
						for (let key in element.faces) {
							if (element.faces[key].texture !== null && element.faces[key].vertices.length >= 3) {
								let face = element.faces[key];
								let vertices = face.getSortedVertices().slice();
								let tex = element.faces[key].getTexture();
								let uv_size = [Project.getUVWidth(tex), Project.getUVHeight(tex)];

								vertices.forEach((vkey) => {
									output.texCoords.push([
										face.uv[vkey][0] / uv_size[0],
										1 - face.uv[vkey][1] / uv_size[1],
									]);
									nbVertexUvs += 1;
								});

								if (element.shading == "flat") {
									normal.fromArray(face.getNormal(true));
									normal.applyMatrix3(normalMatrixWorld).normalize();
									vertexnormals.push([normal.x, normal.y, normal.z]);
									nbNormals += 1;
								}

								if (tex && tex.id && !materials[tex.id]) {
									materials[tex.id] = tex;
								}
								let mtl_new = !tex || typeof tex === "string" ? null : tex.id;
								if (mtl_new != mtl) {
									mtl = mtl_new;
									currentMaterialName = mtl;
								}

								if (face_export_mode == "quads" && vertices.length == 3) vertices.push(vertices[0]);

								let triplets = [];
								vertices.forEach((vkey) => {
									let triplet = [
										vertex_keys.indexOf(vkey) + 1 + indexVertex,
										nbVertexUvs - vertices.length + vertices.indexOf(vkey) + 1 + indexVertexUvs,
										element.shading == "smooth"
											? indexNormals + 1 + vertex_keys.indexOf(vkey)
											: i + 1 + indexNormals,
									];
									triplets.push(triplet);
								});
								pushFace(triplets);

								i++;
							}
						}
						output.normals.push(...vertexnormals);
					} else {
						const vertices = geometry.getAttribute("position");
						const normals = geometry.getAttribute("normal");
						const uvs = geometry.getAttribute("uv");
						const indices = geometry.getIndex();

						currentObjectName = mesh.name;

						if (mesh.material && mesh.material.name) {
							currentMaterialName = mesh.material.name;
						} // vertices

						if (vertices !== undefined) {
							for (let i = 0, l = vertices.count; i < l; i++, nbVertex++) {
								vertex.x = vertices.getX(i);
								vertex.y = vertices.getY(i);
								vertex.z = vertices.getZ(i); // transform the vertex to world space

								vertex.applyMatrix4(mesh.matrixWorld).divideScalar(export_scale); // transform the vertex to export format

								output.vertices.push([vertex.x, vertex.y, vertex.z]);
							}
						} // uvs

						if (uvs !== undefined) {
							for (let i = 0, l = uvs.count; i < l; i++, nbVertexUvs++) {
								uv.x = uvs.getX(i);
								uv.y = uvs.getY(i); // transform the uv to export format

								output.texCoords.push([uv.x, uv.y]);
							}
						} // normals

						if (normals !== undefined) {
							normalMatrixWorld.getNormalMatrix(mesh.matrixWorld);

							for (let i = 0, l = normals.count; i < l; i++, nbNormals++) {
								normal.x = normals.getX(i);
								normal.y = normals.getY(i);
								normal.z = normals.getZ(i); // transform the normal to world space

								normal.applyMatrix3(normalMatrixWorld).normalize(); // transform the normal to export format

								output.normals.push([normal.x, normal.y, normal.z]);
							}
						}

						// material
						for (let key in element.faces) {
							let tex = element.faces[key].getTexture();
							if (tex && tex.id && !materials[tex.id]) {
								materials[tex.id] = tex;
							}
						}

						// faces
						if (indices !== null) {
							for (let i = 0, l = indices.count; i < l; i += 3) {
								let f_mat = getMtlFace(element, geometry.groups[Math.floor(i / 6)].materialIndex);
								if (f_mat !== undefined) {
									if (i % 2 === 0) {
										currentMaterialName = f_mat;
									}

									for (let m = 0; m < 3; m++) {
										const j = indices.getX(i + m) + 1;
										face[m] = (
											indexVertex +
											j +
											(normals || uvs
												? "/" +
												  (uvs ? indexVertexUvs + j : "") +
												  (normals ? "/" + (indexNormals + j) : "")
												: "")
										)
											.split("/")
											.map((i) => parseInt(i));
									} // transform the face to export format

									pushFace(face);
								}
							}
						} else {
							for (let i = 0, l = vertices.count; i < l; i += 3) {
								for (let m = 0; m < 3; m++) {
									const j = i + m + 1;
									face[m] = (
										indexVertex +
										j +
										(normals || uvs
											? "/" +
											  (uvs ? indexVertexUvs + j : "") +
											  (normals ? "/" + (indexNormals + j) : "")
											: "")
									)
										.split("/")
										.map((i) => parseInt(i));
								} // transform the face to export format

								pushFace(face);
							}
						}
					}

					// update index
					indexVertex += nbVertex;
					indexVertexUvs += nbVertexUvs;
					indexNormals += nbNormals;
				};

				scene.traverse(function (child) {
					if (child instanceof THREE.Mesh) parseMesh(child);
				});

				scene.position.copy(old_scene_position);

				function checkExport(key, condition) {
					key = options[key];
					if (key === undefined) {
						return condition;
					} else {
						return key;
					}
				}

				var texturesObj = {};
				Texture.all.forEach(function (t, i) {
					var link = t.javaTextureLink();
					if (t.particle) {
						texturesObj.particle = link;
					}
					if (t.id !== link.replace(/^#/, "")) {
						texturesObj[t.id] = link;
					}
				});

				var blockmodel = {
					data: output,
				};
				if (checkExport("comment", Project.credit || settings.credit.value)) {
					blockmodel.credit = Project.credit || settings.credit.value;
				}
				if (checkExport("textures", Object.keys(texturesObj).length >= 1)) {
					blockmodel.textures = texturesObj;
				}
				if (checkExport("display", Object.keys(Project.display_settings).length >= 1)) {
					var new_display = {};
					var entries = 0;
					for (var i in DisplayMode.slots) {
						var key = DisplayMode.slots[i];
						if (
							DisplayMode.slots.hasOwnProperty(i) &&
							Project.display_settings[key] &&
							Project.display_settings[key].export
						) {
							new_display[key] = Project.display_settings[key].export();
							entries++;
						}
					}
					if (entries) {
						blockmodel.display = new_display;
					}
				}
				for (let key in Project.unhandled_root_fields) {
					if (blockmodel[key] === undefined) blockmodel[key] = Project.unhandled_root_fields[key];
				}
				this.dispatchEvent("compile", { model: blockmodel, options });
				if (options.raw) {
					return blockmodel;
				} else {
					return autoStringify(blockmodel);
				}
			},
		});

		var format = new ModelFormat({
			...Formats.free,
			id: "pswg_model",
			name: "PSWG Model",
			extension: "json",
			icon: "icon-format_java",
			category: "minecraft",
			target: "Minecraft: Java Edition",
			display_mode: true,
			codec,
		});

		codec.format = format;
		codec.export_action = new Action({
			id: "export_pswg_model",
			name: "Export PSWG Model",
			icon: "icon-format_java",
			category: "file",
			condition: () => Format == format,
			click: function () {
				codec.export();
			},
		});

		MenuBar.addAction(codec.export_action, "file.export");
	},
});
