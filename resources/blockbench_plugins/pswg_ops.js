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

				var blockmodel = {};
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
			name: "Export PSWG Model"
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
