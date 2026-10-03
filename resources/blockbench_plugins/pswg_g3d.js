/**
 * A Blockbench workspace for PSWG models.
 */
(function () {
	'use strict';

	const FORMAT_ID = 'pswg_g3d';
	const CODEC_ID = 'pswg_g3d_source';
	const DEFAULT_LAYERS = {
		block: 'minecraft:block/cutout',
		item: 'minecraft:item/cutout',
		entity: 'minecraft:entity/cutout',
	};
	const MENU_PATHS = {
		export: 'file.export',
	};
	const DISPLAY_SLOTS = [
		'thirdperson_righthand',
		'thirdperson_lefthand',
		'firstperson_righthand',
		'firstperson_lefthand',
		'head',
		'ground',
		'fixed',
		'on_shelf',
		'gui',
	];
	const SURFACE_STYLES = {
		solid: 'Opaque',
		cutout: 'Cutout (clear or solid pixels)',
		translucent: 'Translucent (glass)',
		emissive: 'Glow',
		emissive_translucent: 'Glow with transparency',
		custom: 'Keep imported surface settings',
	};
	const TEXTURE_DEFAULTS = {
		g3d_material_id: '',
		g3d_texture: '',
		g3d_layers: DEFAULT_LAYERS,
		g3d_light_emission: 0,
		g3d_double_sided: true,
		g3d_tint_index: -1,
		g3d_preview_target: 'item',
		g3d_settings_ready: false,
	};

	let format;
	let codec;
	let properties = [];
	let actions = [];
	let editingTexture = null;
	const cleanups = [];
	const previewMaterials = new Map();
	const previewSettings = new Map();
	const pendingSettings = new Map();
	const textureDialogs = new Set();

	// Source data checks

	/** Copy plain data so two textures never share an editable settings object. */
	function cloneJson(value) {
		return JSON.parse(JSON.stringify(value));
	}

	/** Stop an import or export with one message that the dialog can show. */
	function fail(message) {
		throw new Error(message);
	}

	/** Require a name or path with at least one visible character. */
	function requiredString(value, description) {
		if (typeof value !== 'string' || value.trim().length === 0) {
			fail(description + ' must be a non-empty string.');
		}
		return value;
	}

	/** Check the resource-path spelling that Minecraft accepts. */
	function identifier(value, description) {
		const result = requiredString(value, description);
		if (!/^(?:[a-z0-9_.-]+:)?[a-z0-9/._-]+$/.test(result)) {
			fail(description + ' must be a valid Minecraft resource identifier.');
		}
		return result;
	}

	/** Read an array or an x/y/z object, with a default for omitted values. */
	function vector(value, size, description, fallback) {
		if (value === undefined || value === null) {
			return fallback.slice();
		}

		let values = value;
		if (!Array.isArray(values) && typeof values === 'object') {
			values = size === 2 ? [value.x, value.y] : [value.x, value.y, value.z];
		}
		if (!Array.isArray(values) || values.length !== size || values.some((item) => !Number.isFinite(item))) {
			fail(description + ' must contain ' + size + ' finite numbers.');
		}
		return values.slice();
	}

	/** Read either rotation shape supported by the G3D source codec. */
	function quaternion(value, description) {
		if (value === undefined || value === null) {
			return [0, 0, 0, 1];
		}
		if (Array.isArray(value)) {
			return vector(value, 4, description, [0, 0, 0, 1]);
		}
		if (typeof value === 'object' && ['x', 'y', 'z', 'w'].every((key) => Number.isFinite(value[key]))) {
			return [value.x, value.y, value.z, value.w];
		}
		if (typeof value === 'object' && value.axis !== undefined && Number.isFinite(value.angle)) {
			const axis = vector(value.axis, 3, description + ' axis', [0, 0, 1]);
			const axisLength = Math.hypot(axis[0], axis[1], axis[2]);
			if (axisLength === 0) {
				return [0, 0, 0, 1];
			}
			const sine = Math.sin(value.angle / 2) / axisLength;
			return [axis[0] * sine, axis[1] * sine, axis[2] * sine, Math.cos(value.angle / 2)];
		}
		fail(description + ' must be an [x, y, z, w] quaternion or an axis-angle value.');
	}

	/** Fill in the identity values for an incomplete source transform. */
	function normalizedTransform(value, description) {
		value = value || {};
		return {
			translation: vector(value.translation, 3, description + ' translation', [0, 0, 0]),
			rotation: quaternion(value.rotation, description + ' rotation'),
			scale: vector(value.scale, 3, description + ' scale', [1, 1, 1]),
		};
	}

	/** Check the vanilla model data that datagen will write to the sidecar. */
	function validateModelMetadata(model) {
		if (!model || typeof model !== 'object' || Array.isArray(model)) {
			fail('Model display settings must be an object.');
		}
		if (model.gui_light !== undefined && !['front', 'side'].includes(model.gui_light)) {
			fail('GUI lighting must be front or side.');
		}
		if (model.display === undefined) return;
		if (!model.display || typeof model.display !== 'object' || Array.isArray(model.display)) {
			fail('Display settings must contain named views.');
		}
		for (const [name, slot] of Object.entries(model.display)) {
			if (!slot || typeof slot !== 'object' || Array.isArray(slot)) {
				fail('The ' + name + ' display view must be an object.');
			}
			for (const field of ['rotation', 'translation', 'scale']) {
				vector(slot[field], 3, name + ' display ' + field, field === 'scale' ? [1, 1, 1] : [0, 0, 0]);
			}
		}
	}

	/** Fill in material defaults and check the values that will reach the game. */
	function normalizeMaterial(material) {
		const id = requiredString(material.id, 'Material id');
		const texture = identifier(material.texture, 'Material ' + id + ' texture');
		const layers = material.layers || {};
		const result = {
			id,
			texture,
			layers: {
				block: layers.block || DEFAULT_LAYERS.block,
				item: layers.item || DEFAULT_LAYERS.item,
				entity: layers.entity || DEFAULT_LAYERS.entity,
			},
			tintIndex: material.tintIndex === undefined ? -1 : material.tintIndex,
			lightEmission: material.lightEmission === undefined ? 0 : material.lightEmission,
			doubleSided: material.doubleSided === undefined ? false : material.doubleSided,
		};

		if (!Number.isInteger(result.tintIndex) || result.tintIndex < -1 || result.tintIndex > 255) {
			fail('Material ' + id + ' tintIndex must be an integer from -1 to 255.');
		}
		if (!Number.isInteger(result.lightEmission) || result.lightEmission < 0 || result.lightEmission > 15) {
			fail('Material ' + id + ' lightEmission must be an integer from 0 to 15.');
		}
		if (typeof result.doubleSided !== 'boolean') {
			fail('Material ' + id + ' doubleSided must be true or false.');
		}
		for (const target of ['block', 'item', 'entity']) {
			identifier(result.layers[target], 'Material ' + id + ' ' + target + ' layer');
		}
		return result;
	}

	/** Check the whole source before adding anything to the editor. */
	function validateSource(source) {
		if (!source || source.version !== 1) {
			fail('Only G3D source version 1 can be opened.');
		}
		for (const key of ['materials', 'nodes', 'meshes']) {
			if (!Array.isArray(source[key])) {
				fail('G3D source field "' + key + '" must be an array.');
			}
		}
		if (source.sockets !== undefined && !Array.isArray(source.sockets)) {
			fail('G3D source field "sockets" must be an array.');
		}
		if (source.model !== undefined) validateModelMetadata(source.model);

		/** Index a source table and reject names that could mean two entries. */
		function makeNameMap(entries, label) {
			const result = new Map();
			for (const entry of entries) {
				const id = requiredString(entry.id, label + ' id');
				if (result.has(id)) {
					fail('Duplicate ' + label + ' id "' + id + '".');
				}
				result.set(id, entry);
			}
			return result;
		}

		const materials = source.materials.map(normalizeMaterial);
		const materialIds = new Set();
		materials.forEach((material) => {
			if (materialIds.has(material.id)) fail('Duplicate material id "' + material.id + '".');
			materialIds.add(material.id);
		});
		const nodes = makeNameMap(source.nodes, 'node');
		const meshes = makeNameMap(source.meshes, 'mesh');
		const sockets = makeNameMap(source.sockets || [], 'socket');
		const children = new Map();
		for (const node of source.nodes) {
			if (!Array.isArray(node.meshes || [])) {
				fail('Node ' + node.id + ' meshes must be an array.');
			}
			const parent = node.parent;
			if (parent !== undefined && !nodes.has(parent)) {
				fail('Node ' + node.id + ' references missing parent "' + parent + '".');
			}
			if (parent !== undefined) {
				if (!children.has(parent)) children.set(parent, []);
				children.get(parent).push(node.id);
			}
			normalizedTransform(node.restTransform, 'Node ' + node.id + ' restTransform');
		}

		const origins = new Map();
		// Visit parents first. This also catches a loop in the group hierarchy.
		const ready = source.nodes.filter((node) => node.parent === undefined).map((node) => node.id);
		while (ready.length) {
			const id = ready.shift();
			const node = nodes.get(id);
			const transform = normalizedTransform(node.restTransform, 'Node ' + id + ' restTransform');
			const parentOrigin = node.parent === undefined ? [0, 0, 0] : origins.get(node.parent);
			origins.set(
				id,
				parentOrigin.map((value, axis) => value + transform.translation[axis]),
			);
			for (const child of children.get(id) || []) ready.push(child);
		}
		if (origins.size !== source.nodes.length) {
			fail('Node hierarchy contains a cycle.');
		}

		for (const mesh of source.meshes) {
			if (!materialIds.has(mesh.material)) {
				fail('Mesh ' + mesh.id + ' references missing material "' + mesh.material + '".');
			}
			if (!Array.isArray(mesh.vertices) || !Array.isArray(mesh.indices) || mesh.indices.length % 3 !== 0) {
				fail('Mesh ' + mesh.id + ' must contain vertex and triangle-index arrays.');
			}
			mesh.vertices.forEach((vertex, index) => {
				vector(vertex.position, 3, 'Mesh ' + mesh.id + ' vertex ' + index + ' position', [0, 0, 0]);
				vector(vertex.normal, 3, 'Mesh ' + mesh.id + ' vertex ' + index + ' normal', [0, 1, 0]);
				vector(vertex.uv, 2, 'Mesh ' + mesh.id + ' vertex ' + index + ' uv', [0, 0]);
			});
			mesh.indices.forEach((index) => {
				if (!Number.isInteger(index) || index < 0 || index >= mesh.vertices.length) {
					fail('Mesh ' + mesh.id + ' contains an out-of-range triangle index.');
				}
			});
		}
		for (const node of source.nodes) {
			for (const mesh of node.meshes || []) {
				if (!meshes.has(mesh)) fail('Node ' + node.id + ' references missing mesh "' + mesh + '".');
			}
		}
		for (const socket of source.sockets || []) {
			if (!nodes.has(socket.node))
				fail('Socket ' + socket.id + ' references missing node "' + socket.node + '".');
			normalizedTransform(socket.localTransform, 'Socket ' + socket.id + ' localTransform');
		}

		return {materials, nodes, meshes, origins, sockets};
	}

	// Import and export helpers

	/** Turn a source rotation into the degree values used by Blockbench. */
	function toBlockbenchEuler(quaternionValues) {
		const rotation = new THREE.Quaternion(...quaternionValues);
		if (rotation.lengthSq() === 0) rotation.identity();
		else rotation.normalize();
		const euler = new THREE.Euler().setFromQuaternion(rotation, 'XYZ');
		return [euler.x, euler.y, euler.z].map((value) => (value * 180) / Math.PI);
	}

	/** Turn Blockbench's degree values back into a source rotation. */
	function fromBlockbenchEuler(degrees) {
		const euler = new THREE.Euler(
			(degrees[0] * Math.PI) / 180,
			(degrees[1] * Math.PI) / 180,
			(degrees[2] * Math.PI) / 180,
			'XYZ',
		);
		const rotation = new THREE.Quaternion().setFromEuler(euler).normalize();
		return rotation.toArray();
	}

	/** Split a resource name and supply a namespace when it has none. */
	function resourceParts(identifier, defaultNamespace) {
		const separator = identifier.indexOf(':');
		return separator < 0
			? [defaultNamespace, identifier]
			: [identifier.slice(0, separator), identifier.slice(separator + 1)];
	}

	/** Keep the game image path separate from the texture's editable display name. */
	function textureResourceId(texture) {
		if (texture.g3d_texture) {
			return identifier(texture.g3d_texture, 'Game texture for ' + texture.name);
		}
		const link = requiredString(texture.javaTextureLink(), 'Blockbench texture resource path').replace(
			/^#/,
			'',
		);
		const [namespace, resourcePath] = resourceParts(link, texture.namespace || 'minecraft');
		const path = resourcePath.replace(/^textures\//, '').replace(/\.(?:png|tga|webp)$/i, '');
		return identifier(namespace + ':textures/' + path + '.png', 'Blockbench texture resource path');
	}

	/** Suggest a readable internal name; the artist does not have to enter it. */
	function textureMaterialId(texture) {
		const texturePath = texture
			.javaTextureLink()
			.toLowerCase()
			.replace(/[^a-z0-9_.-]+/g, '_')
			.replace(/^_+|_+$/g, '');
		return 'texture_' + (texturePath || texture.uuid.replace(/[^a-z0-9_.-]+/gi, '_'));
	}

	/** Find the resource-pack root when a model sits in its usual assets folder. */
	function resourceRootFor(sourcePath) {
		if (!sourcePath) return null;
		const path = sourcePath.replace(/\\/g, '/');
		const match = path.match(/^(.*)\/assets\/[^/]+\/g3d\/source\//);
		return match ? match[1] : null;
	}

	/** Read an older model's sidecar so its display and particle choices carry over. */
	function readLocalSidecar(sourcePath) {
		const root = resourceRootFor(sourcePath);
		const match = sourcePath?.replace(/\\/g, '/').match(/\/assets\/([^/]+)\/g3d\/source\/(.+)\.jg3d$/);
		if (!root || !match) return {};
		const path = resolveAssetFile(root, match[1], 'models/' + match[2], '.json');
		if (!path) return {};
		return JSON.parse(fs.readFileSync(path, 'utf8'));
	}

	/** Merge locally available parents for the same view the game will inherit. */
	function inheritedDisplayMetadata(model, sourcePath) {
		const result = cloneJson(model);
		const visited = new Set();
		const root = resourceRootFor(sourcePath);
		let parent = model.parent;
		while (root && parent && !visited.has(parent) && visited.size < 32) {
			visited.add(parent);
			const [namespace, resourcePath] = resourceParts(parent, 'minecraft');
			const path = resolveAssetFile(root, namespace, 'models/' + resourcePath, '.json');
			if (!path) break;
			const definition = JSON.parse(fs.readFileSync(path, 'utf8'));
			result.display = {...definition.display, ...result.display};
			if (result.gui_light === undefined) result.gui_light = definition.gui_light;
			if (result.ambientocclusion === undefined) result.ambientocclusion = definition.ambientocclusion;
			parent = definition.parent;
		}
		return result;
	}

	/** Load native display slots and keep the other sidecar fields as plain data. */
	function importModelMetadata(source, sourcePath) {
		const model = source.model === undefined ? readLocalSidecar(sourcePath) : source.model;
		validateModelMetadata(model);
		Project.g3d_model_metadata = cloneJson(model);
		delete Project.g3d_model_metadata.elements;
		const inherited = inheritedDisplayMetadata(model, sourcePath);
		Project.display_settings = {};
		DisplayMode.loadJSON(inherited.display || {});
		// Vanilla falls back to the right-hand slot when the left-hand slot is absent.
		for (const kind of ['firstperson', 'thirdperson']) {
			const right = Project.display_settings[kind + '_righthand'];
			if (right && !Project.display_settings[kind + '_lefthand']) {
				Project.display_settings[kind + '_lefthand'] = new DisplaySlot(kind + '_lefthand', right.copy());
			}
		}
		Project.front_gui_light = inherited.gui_light === 'front';
		Project.ambientocclusion = inherited.ambientocclusion !== false;
		Project.parent = model.parent || '';
		if (Project.shelf_align_bottom === undefined) Project.shelf_align_bottom = true;
	}

	/** Use the surface's atlas sprite when the artist selects a particle texture. */
	function particleSpriteForTexture(texture) {
		const resource = textureResourceId(texture);
		const [namespace, resourcePath] = resourceParts(resource, 'minecraft');
		const root = resourceRootFor(Project.export_path);
		const ptex = resolveAssetFile(root, namespace, 'ptex/' + resourcePath, '.json');
		if (ptex || !resourcePath.startsWith('textures/') || !resourcePath.endsWith('.png')) {
			return namespace + ':ptex/' + resourcePath;
		}
		return namespace + ':' + resourcePath.slice('textures/'.length, -'.png'.length);
	}

	/** Export native display values, including explicit identity views after a reset. */
	function exportModelMetadata() {
		const model = cloneJson(Project.g3d_model_metadata || {});
		delete model.elements;
		model.display = {};
		for (const name of DISPLAY_SLOTS) {
			const slot = Project.display_settings[name] || new DisplaySlot(name);
			const data = slot.export() || {};
			// Keep each view explicit so a parent cannot undo the artist's reset.
			model.display[name] = {
				rotation: vector(data.rotation, 3, name + ' rotation', [0, 0, 0]),
				translation: vector(data.translation, 3, name + ' translation', [0, 0, 0]),
				scale: vector(data.scale, 3, name + ' scale', [1, 1, 1]),
			};
		}
		model.gui_light = Project.front_gui_light ? 'front' : 'side';
		model.ambientocclusion = Project.ambientocclusion !== false;
		if (Project.parent) model.parent = Project.parent;
		const particle = Texture.all.find((texture) => texture.particle);
		if (particle) {
			model.textures = model.textures || {};
			model.textures.particle = particleSpriteForTexture(particle);
		}
		return model;
	}

	/** Look up a local asset without letting its path escape the assets folder. */
	function resolveAssetFile(resourceRoot, namespace, resourcePath, extension) {
		if (!resourceRoot || !isApp || typeof fs === 'undefined') return null;
		const segments = resourcePath
			.split('/')
			.filter((segment) => segment && segment !== '.' && segment !== '..');
		let path = PathModule.join(resourceRoot, 'assets', namespace, ...segments);
		if (extension && !path.toLowerCase().endsWith(extension.toLowerCase())) path += extension;
		return fs.existsSync(path) ? path : null;
	}

	/** Find an image for a direct texture or a simple Ptex source graph. */
	function resolvePreviewImage(identifier, sourcePath) {
		const resourceRoot = resourceRootFor(sourcePath);
		if (!resourceRoot || !isApp || typeof fs === 'undefined') return null;
		const [namespace, resourcePath] = resourceParts(identifier, 'minecraft');
		const ptexFile = resolveAssetFile(resourceRoot, namespace, 'ptex/' + resourcePath, '.json');
		if (ptexFile) {
			try {
				const definition = JSON.parse(fs.readFileSync(ptexFile, 'utf8'));
				const graph = definition.graph;
				if (!graph || graph.type !== 'pswg:source' || typeof graph.texture !== 'string') return null;
				const [textureNamespace, texturePath] = resourceParts(graph.texture, namespace);
				const textureFile = resolveAssetFile(resourceRoot, textureNamespace, texturePath, '.png');
				if (!textureFile) return null;
				return textureFile;
			} catch (error) {
				console.warn('[PSWG G3D] Could not preview Ptex material ' + identifier + ':', error);
				return null;
			}
		}

		if (resourcePath.startsWith('textures/') && resourcePath.endsWith('.png')) {
			const directImage = resolveAssetFile(resourceRoot, namespace, resourcePath);
			if (directImage) return directImage;
		}
		return null;
	}

	/** Copy an imported material onto one texture. The image can stay missing. */
	function setTextureMaterial(texture, material) {
		Object.assign(texture, {
			g3d_material_id: material.id,
			g3d_texture: material.texture,
			g3d_layers: cloneJson(material.layers),
			g3d_light_emission: material.lightEmission,
			g3d_double_sided: material.doubleSided,
			g3d_tint_index: material.tintIndex,
			g3d_settings_ready: true,
		});
	}

	/** Create a separate texture for each material, including shared images. */
	function importMaterialTexture(material, sourcePath) {
		const [namespace, resourcePath] = resourceParts(material.texture, 'minecraft');
		const imagePath = resourcePath.replace(/^textures\//, '');
		const parts = imagePath.split('/');
		const texture = new Texture({
			name: parts.pop() + (imagePath.endsWith('.png') ? '' : '.png'),
			folder: parts.join('/'),
			namespace,
			keep_size: true,
		});
		setTextureMaterial(texture, material);
		const image = resolvePreviewImage(material.texture, sourcePath);
		if (image) {
			// This loader keeps the resource name above and does not merge images.
			texture.loadContentFromPath(image);
		} else {
			// A real Texture keeps the binding editable with Change File or painting.
			texture.loadEmpty();
		}
		texture.add(false);
		texture.updateMaterial();
		return texture;
	}

	/** Open source groups, triangles, and sockets as native editor objects. */
	function importSource(source, sourcePath) {
		const data = validateSource(source);
		importModelMetadata(source, sourcePath);
		const materialTextures = new Map(
			data.materials.map((material) => [material.id, importMaterialTexture(material, sourcePath)]),
		);
		Project.g3d_materials = [];

		const groups = new Map();
		for (const node of source.nodes) {
			const transform = normalizedTransform(node.restTransform, 'Node ' + node.id + ' restTransform');
			const group = new Group({
				name: node.id,
				origin: data.origins.get(node.id),
				rotation: toBlockbenchEuler(transform.rotation),
				g3d_node_id: node.id,
				g3d_scale: transform.scale,
			});
			groups.set(node.id, group);
		}
		for (const node of source.nodes) {
			const group = groups.get(node.id);
			group.addTo(node.parent === undefined ? 'root' : groups.get(node.parent)).init();
		}
		for (const group of groups.values()) {
			group.preview_controller.updateTransform(group);
		}

		const meshInstances = new Map();
		for (const node of source.nodes) {
			const group = groups.get(node.id);
			for (const sourceMeshId of node.meshes || []) {
				const sourceMesh = data.meshes.get(sourceMeshId);
				const instance = (meshInstances.get(sourceMeshId) || 0) + 1;
				meshInstances.set(sourceMeshId, instance);
				const meshId = instance === 1 ? sourceMeshId : sourceMeshId + '_instance_' + instance;
				const mesh = new Mesh({
					name: meshId,
					origin: data.origins.get(node.id),
					shading: 'smooth',
					g3d_mesh_id: meshId,
					g3d_vertex_source: {},
				});
				const vertexKeys = mesh.addVertices(...sourceMesh.vertices.map((vertex) => vertex.position.slice()));
				const sourceNormals = {};
				// Keep original normals until an artist moves their vertices.
				sourceMesh.vertices.forEach((vertex, index) => {
					sourceNormals[vertexKeys[index]] = {
						position: vertex.position.slice(),
						normal: vertex.normal.slice(),
					};
				});
				mesh.g3d_vertex_source = sourceNormals;
				const texture = materialTextures.get(sourceMesh.material);
				const scaleU = Project.texture_width || 16;
				const scaleV = Project.texture_height || 16;
				// G3D stores fractions of an image; Blockbench edits UVs in pixels.
				for (let index = 0; index < sourceMesh.indices.length; index += 3) {
					const indices = sourceMesh.indices.slice(index, index + 3);
					const keys = indices.map((vertexIndex) => vertexKeys[vertexIndex]);
					const uv = {};
					indices.forEach((vertexIndex, corner) => {
						uv[keys[corner]] = sourceMesh.vertices[vertexIndex].uv.map(
							(value, axis) => value * (axis === 0 ? scaleU : scaleV),
						);
					});
					const face = new MeshFace(mesh, {
						vertices: keys,
						uv,
						texture: texture ? texture.uuid : null,
					});
					face.texture = texture ? texture.uuid : null;
					mesh.addFaces(face);
				}
				mesh.addTo(group).init();
			}
		}

		for (const socket of source.sockets || []) {
			const transform = normalizedTransform(socket.localTransform, 'Socket ' + socket.id + ' localTransform');
			const nodeOrigin = data.origins.get(socket.node);
			const locator = new Locator({
				name: socket.id,
				position: nodeOrigin.map((value, axis) => value + transform.translation[axis]),
				rotation: toBlockbenchEuler(transform.rotation),
				g3d_socket_id: socket.id,
				g3d_scale: transform.scale,
			});
			locator.addTo(groups.get(socket.node)).init();
		}

		Canvas.updateAllPositions();
		Canvas.updateAllBones();
		Canvas.updateAllFaces();
		UVEditor.loadData();
		codec.dispatchEvent('parsed', {model: source});
	}

	/** Add a short suffix when a copied object has the same internal name. */
	function uniqueId(id, used) {
		const base = requiredString(id, 'G3D identifier');
		if (!used.has(base)) {
			used.add(base);
			return base;
		}
		let suffix = 2;
		while (used.has(base + '_' + suffix)) suffix++;
		const result = base + '_' + suffix;
		used.add(result);
		return result;
	}

	/** Measure a child position from its parent's origin. */
	function subtract(a, b) {
		return [a[0] - b[0], a[1] - b[1], a[2] - b[2]];
	}

	/** Ignore tiny rounding differences when deciding if a vertex has moved. */
	function sameVector(a, b) {
		return (
			Array.isArray(a) &&
			Array.isArray(b) &&
			a.length === b.length &&
			a.every((value, index) => Math.abs(value - b[index]) < 0.000001)
		);
	}

	/** Find a flat triangle normal. A collapsed triangle gets a zero normal. */
	function triangleNormal(a, b, c) {
		const ab = [b[0] - a[0], b[1] - a[1], b[2] - a[2]];
		const ac = [c[0] - a[0], c[1] - a[1], c[2] - a[2]];
		const normal = [
			ab[1] * ac[2] - ab[2] * ac[1],
			ab[2] * ac[0] - ab[0] * ac[2],
			ab[0] * ac[1] - ab[1] * ac[0],
		];
		const length = Math.hypot(...normal);
		return length === 0 ? [0, 0, 0] : normal.map((value) => value / length);
	}

	/** Bake one texture's faces into triangles in the parent group's space. */
	function exportMesh(element, meshId, materialId, faces) {
		const meshTransform = element.mesh;
		if (meshTransform) meshTransform.updateMatrix();
		const transform = meshTransform ? meshTransform.matrix : new THREE.Matrix4();
		const normalMatrix = new THREE.Matrix3().getNormalMatrix(transform);
		const smoothNormals = element.shading === 'smooth' ? element.calculateNormals() : {};
		const sourceNormals = element.g3d_vertex_source || {};
		const outputVertices = [];
		const outputIndices = [];
		const vertexLookup = new Map();
		const textureWidth = Project.texture_width || 16;
		const textureHeight = Project.texture_height || 16;

		/** Share corners only when their position, normal, and UV all match. */
		function appendVertex(vertexKey, triangle) {
			const position = element.vertices[vertexKey];
			if (!position) fail('Mesh ' + element.name + ' references a missing vertex.');
			const transformed = new THREE.Vector3(...position).applyMatrix4(transform).toArray();
			const uvPixels = triangle.face.uv[vertexKey] || [0, 0];
			const uv = [uvPixels[0] / textureWidth, uvPixels[1] / textureHeight];
			const source = sourceNormals[vertexKey];
			const preserved = source && sameVector(source.position, position);
			let normal;
			if (preserved) {
				normal = new THREE.Vector3(...source.normal).applyMatrix3(normalMatrix).toArray();
			} else {
				const localNormal =
					element.shading === 'smooth' && smoothNormals[vertexKey]
						? smoothNormals[vertexKey]
						: triangleNormal(
								element.vertices[triangle.keys[0]],
								element.vertices[triangle.keys[1]],
								element.vertices[triangle.keys[2]],
							);
				normal = new THREE.Vector3(...localNormal).applyMatrix3(normalMatrix).normalize().toArray();
			}
			const lookupKey = JSON.stringify([vertexKey, transformed, normal, uv]);
			if (!vertexLookup.has(lookupKey)) {
				vertexLookup.set(lookupKey, outputVertices.length);
				outputVertices.push({position: transformed, normal, uv});
			}
			return vertexLookup.get(lookupKey);
		}

		for (const face of faces) {
			const faceVertices = face.getSortedVertices ? face.getSortedVertices() : face.vertices;
			if (!Array.isArray(faceVertices) || faceVertices.length < 3) continue;
			for (let corner = 1; corner < faceVertices.length - 1; corner++) {
				const triangle = {
					face,
					keys: [faceVertices[0], faceVertices[corner], faceVertices[corner + 1]],
				};
				outputIndices.push(...triangle.keys.map((key) => appendVertex(key, triangle)));
			}
		}

		return {
			id: meshId,
			material: materialId,
			vertices: outputVertices,
			indices: outputIndices,
		};
	}

	/** Read each face's Texture. An old mesh-level binding must not override it. */
	function meshMaterialGroups(mesh, materialsByTexture) {
		const faces = Object.values(mesh.faces).filter(
			(face) => Array.isArray(face.vertices) && face.vertices.length >= 3,
		);
		if (!faces.length) return [];

		const groupedFaces = new Map();
		const untexturedFaces = [];
		for (const face of faces) {
			const texture = face.getTexture ? face.getTexture() : null;
			if (!texture) {
				untexturedFaces.push(face);
				continue;
			}
			const material = materialsByTexture.get(texture);
			if (!material)
				fail('The texture on ' + mesh.name + ' is no longer in this project. Apply a texture again.');
			if (!groupedFaces.has(material.id)) groupedFaces.set(material.id, []);
			groupedFaces.get(material.id).push(face);
		}

		if (untexturedFaces.length) {
			let fallbackId;
			if (groupedFaces.size === 1) {
				fallbackId = groupedFaces.keys().next().value;
			} else {
				const defaultTexture = Texture.getDefault() || Texture.selected || Texture.all[0];
				if (defaultTexture) fallbackId = materialsByTexture.get(defaultTexture)?.id;
			}
			if (!fallbackId) {
				fail('Add a texture and apply it to ' + mesh.name + ' before export.');
			}
			if (!groupedFaces.has(fallbackId)) groupedFaces.set(fallbackId, []);
			groupedFaces.get(fallbackId).push(...untexturedFaces);
		}

		return Array.from(groupedFaces, ([materialId, grouped]) => ({materialId, faces: grouped}));
	}

	/** Build the source tables from normal names, groups, and face textures. */
	function compileSource() {
		migrateLegacyMaterials();
		const unsupportedCubes = Cube.all.filter((cube) => {
			if (cube.export === false) return false;
			let parent = cube.parent;
			while (parent instanceof Group) {
				if (parent.export === false) return false;
				parent = parent.parent;
			}
			return true;
		});
		if (unsupportedCubes.length) {
			fail('Select the remaining cubes and use Tools → Convert to Mesh. Then export again.');
		}

		const materialsByTexture = new Map();
		for (const texture of Texture.all) {
			ensureTextureSettings(texture);
			ensureTextureMaterialId(texture);
			materialsByTexture.set(texture, materialFromTexture(texture));
		}

		const allGroups = Group.all.slice();
		const visibleGroups = allGroups.filter((group) => {
			let parent = group;
			while (parent instanceof Group) {
				if (parent.export === false) return false;
				parent = parent.parent;
			}
			return true;
		});
		const groupSet = new Set(visibleGroups);
		const looseMeshes = Mesh.all.filter((mesh) => mesh.parent === 'root' && mesh.export !== false);
		const looseLocators = Locator.all.filter(
			(locator) => locator.parent === 'root' && locator.export !== false,
		);
		let syntheticRoot = null;
		if (looseMeshes.length || looseLocators.length) {
			// Artists can leave objects at the top level. Give them a group on export.
			syntheticRoot = {
				name: 'root',
				g3d_node_id: 'root',
				origin: [0, 0, 0],
				rotation: [0, 0, 0],
				g3d_scale: [1, 1, 1],
				parent: 'root',
				children: looseMeshes.concat(looseLocators),
			};
		}

		const nodes = [];
		const meshes = [];
		const sockets = [];
		const usedNodeIds = new Set();
		const usedMeshIds = new Set();
		const usedSocketIds = new Set();
		const nodeIds = new Map();
		const orderedGroups = syntheticRoot ? [syntheticRoot, ...visibleGroups] : visibleGroups;

		for (const group of orderedGroups) {
			const nodeId = uniqueId(group.name || group.g3d_node_id, usedNodeIds);
			nodeIds.set(group, nodeId);
		}

		for (const group of orderedGroups) {
			const isSynthetic = group === syntheticRoot;
			const parent = isSynthetic
				? null
				: group.parent instanceof Group && groupSet.has(group.parent)
					? group.parent
					: null;
			const nodeId = nodeIds.get(group);
			const nodeMeshes = [];
			const children = isSynthetic
				? group.children
				: group.children.filter((child) => child.export !== false);

			for (const element of children) {
				if (element instanceof Mesh) {
					const materialGroups = meshMaterialGroups(element, materialsByTexture);
					const baseMeshId = element.name || element.g3d_mesh_id;
					materialGroups.forEach((group) => {
						const materialSuffix = group.materialId.replace(/[^a-z0-9_.-]+/gi, '_');
						const name = materialGroups.length === 1 ? baseMeshId : baseMeshId + '_' + materialSuffix;
						const meshId = uniqueId(name, usedMeshIds);
						meshes.push(exportMesh(element, meshId, group.materialId, group.faces));
						nodeMeshes.push(meshId);
					});
				} else if (element instanceof Locator) {
					const socketId = uniqueId(element.name || element.g3d_socket_id, usedSocketIds);
					const nodeOrigin = group.origin || [0, 0, 0];
					sockets.push({
						id: socketId,
						node: nodeId,
						localTransform: {
							translation: subtract(element.position, nodeOrigin),
							rotation: fromBlockbenchEuler(element.rotation),
							scale: element.g3d_scale || [1, 1, 1],
						},
					});
				}
			}

			const transform = isSynthetic
				? {translation: [0, 0, 0], rotation: [0, 0, 0, 1], scale: [1, 1, 1]}
				: {
						translation: parent ? subtract(group.origin, parent.origin) : group.origin.slice(),
						rotation: fromBlockbenchEuler(group.rotation),
						scale: group.g3d_scale || [1, 1, 1],
					};
			const node = {id: nodeId, restTransform: transform, meshes: nodeMeshes};
			if (parent) node.parent = nodeIds.get(parent);
			nodes.push(node);
		}

		const result = {
			version: 1,
			materials: Array.from(materialsByTexture.values()),
			nodes,
			meshes,
			sockets,
			model: exportModelMetadata(),
		};
		const geometryByteLimit = 64 * 1024 * 1024;
		let geometryBytes = 4;
		for (const mesh of meshes) {
			geometryBytes += 16 + mesh.vertices.length * 32 + mesh.indices.length * 4;
			if (geometryBytes > geometryByteLimit) {
				fail('Compiled G3D geometry exceeds the V1 64 MiB section limit.');
			}
		}
		validateSource(result);
		return result;
	}

	/** Show an actionable import, export, or settings error in Blockbench. */
	function reportError(title, error) {
		Blockbench.showMessageBox({
			title,
			message: error && error.message ? error.message : String(error),
		});
	}

	// Texture-owned materials and native editor adapters

	/** Keep every extension scoped to the active G3D workspace. */
	function isG3dProject() {
		return format && typeof Format !== 'undefined' && Format?.id === FORMAT_ID;
	}

	/** Give old or newly created textures their first set of surface settings. */
	function ensureTextureSettings(texture) {
		for (const [key, value] of Object.entries(TEXTURE_DEFAULTS)) {
			if (texture[key] === undefined) texture[key] = cloneJson(value);
		}
		if (!texture.g3d_settings_ready) {
			texture.g3d_light_emission = texture.render_mode === 'emissive' ? 15 : 0;
			texture.g3d_layers = layersForStyle(
				texture.render_mode === 'additive' ? 'emissive_translucent' : 'cutout',
			);
			if (texture.render_mode === 'additive') texture.g3d_light_emission = 15;
			texture.g3d_double_sided = texture.render_sides !== 'front';
			texture.g3d_settings_ready = true;
		}
		if (!texture.g3d_texture && texture.name) {
			try {
				texture.g3d_texture = textureResourceId(texture);
			} catch (error) {
				// Let the artist finish a new texture before checking its export path.
			}
		}
	}

	/** Keep a stable ID on each instance. Native Duplicate copies the settings. */
	function ensureTextureMaterialId(texture) {
		const used = new Set(
			Texture.all
				.filter((candidate) => candidate !== texture)
				.map((candidate) => candidate.g3d_material_id)
				.filter(Boolean),
		);
		texture.g3d_material_id = uniqueId(texture.g3d_material_id || textureMaterialId(texture), used);
	}

	/** Give duplicates distinct list names while keeping the same game image. */
	function ensureTextureDisplayName(texture) {
		if (!texture.name || Texture.all.includes(texture)) return;
		const names = new Set(Texture.all.map((candidate) => candidate.name));
		const match = texture.name.match(/^(.*?)(\.[^.]+)?$/);
		let suffix = 2;
		while (names.has(texture.name)) {
			texture.name = match[1] + '_' + suffix++ + (match[2] || '');
		}
	}

	/** Build a material from saved texture data, never from temporary preview data. */
	function materialFromTexture(texture) {
		return normalizeMaterial({
			id: texture.g3d_material_id,
			texture: textureResourceId(texture),
			layers: texture.g3d_layers,
			lightEmission: texture.g3d_light_emission,
			doubleSided: texture.g3d_double_sided,
			tintIndex: texture.g3d_tint_index,
		});
	}

	/** Map a friendly surface choice to the three native Minecraft layers. */
	function layersForStyle(style) {
		const surface = style === 'solid' ? 'solid' : style.includes('translucent') ? 'translucent' : 'cutout';
		return {
			block: 'minecraft:block/' + surface,
			item: 'minecraft:item/' + surface,
			entity: 'minecraft:entity/' + (style === 'emissive_translucent' ? 'translucent_emissive' : surface),
		};
	}

	/** Find the closest preset without changing imported, target-specific layers. */
	function styleForTexture(texture) {
		for (const style of ['solid', 'cutout', 'translucent', 'emissive_translucent']) {
			const layers = layersForStyle(style);
			if (Object.keys(DEFAULT_LAYERS).every((target) => texture.g3d_layers[target] === layers[target])) {
				return style === 'cutout' && texture.g3d_light_emission > 0 ? 'emissive' : style;
			}
		}
		return 'custom';
	}

	/** Upgrade projects saved by the earlier, project-table version of this plugin. */
	function migrateLegacyMaterials() {
		if (!isG3dProject() || !Project.g3d_materials?.length) return;
		const claimed = new Set();
		for (const source of Project.g3d_materials) {
			const material = normalizeMaterial(source);
			let texture = Texture.all.find((candidate) => candidate.g3d_material_id === material.id);
			if (!texture) {
				texture = Texture.all.find((candidate) => {
					if (claimed.has(candidate)) return false;
					try {
						return textureResourceId(candidate) === material.texture;
					} catch (error) {
						return false;
					}
				});
			}
			if (!texture) texture = importMaterialTexture(material, Project.export_path);
			setTextureMaterial(texture, material);
			claimed.add(texture);
			texture.updateMaterial();
			for (const mesh of Mesh.all) {
				if (mesh.g3d_material_id !== material.id) continue;
				mesh.applyTexture(texture, true);
				mesh.g3d_material_id = '';
			}
		}
		Project.g3d_materials = [];
		Project.saved = false;
		Canvas.updateAllFaces();
	}

	/**
	 * Let the native loaders do their work without merging equal file paths.
	 * Blockbench 5.2 has no switch for this. Its fromPath and add checks are
	 * synchronous, so hide only the matching paths and restore them in finally.
	 * The textures, images, file watchers, and undo records stay native.
	 */
	function withDistinctTexturePaths(texture, imagePath, operation) {
		if (!imagePath) return operation();
		const matches = Texture.all
			.filter((candidate) => candidate !== texture && candidate.path === imagePath)
			.map((candidate) => [candidate, candidate.path]);
		try {
			for (const [candidate] of matches) candidate.path = '';
			return operation();
		} finally {
			for (const [candidate, path] of matches) candidate.path = path;
		}
	}

	/** Wrap a native method and restore it when this plugin unloads. */
	function patchMethod(target, name, wrap) {
		const original = target[name];
		const replacement = wrap(original);
		target[name] = replacement;
		cleanups.push(() => {
			// Leave a later plugin's wrapper in place if it has taken this slot.
			if (target[name] === replacement) target[name] = original;
		});
	}

	/** Listen through Blockbench's event system and keep a matching cleanup. */
	function listen(target, event, callback) {
		target.on(event, callback);
		cleanups.push(() => target.removeListener(event, callback));
	}

	/**
	 * Add glow and alpha controls to the native texture shader.
	 * Keep its vertex lighting, selection highlight, UVs, and clipping. Native
	 * Emissive treats alpha as a glow mask; G3D uses alpha for transparency.
	 */
	function installPreviewShader(texture, material) {
		if (previewMaterials.has(material)) return previewMaterials.get(material);
		const state = {
			texture,
			fragmentShader: material.fragmentShader,
			customProgramCacheKey: material.customProgramCacheKey,
			transparent: material.transparent,
			depthWrite: material.depthWrite,
			side: material.side,
			blending: material.blending,
			uniforms: {
				G3D_SURFACE: {value: 1},
				G3D_GLOW: {value: 0},
			},
		};
		// Release builds minify GLSL. Match tokens, not the source file's spaces.
		// Put the new uniforms on the material itself, where Three uploads them.
		Object.assign(material.uniforms, state.uniforms);
		material.fragmentShader = material.fragmentShader
			.replace(
				/uniform\s+bool\s+EMISSIVE\s*;/,
				`uniform bool EMISSIVE;
uniform float G3D_SURFACE;
uniform float G3D_GLOW;`,
			)
			.replace(
				/if\s*\(\s*color\.a\s*<\s*0?\.01\s*\)\s*discard\s*;/,
				`// Opaque ignores alpha. Cutout keeps only the solid pixels.
\tif (G3D_SURFACE < 0.5) {
\t\tcolor.a = 1.0;
\t} else if (G3D_SURFACE < 1.5) {
\t\tif (color.a < 0.1) discard;
\t\tcolor.a = 1.0;
\t} else if (color.a < 0.01) discard;`,
			)
			.replace(
				/if\s*\(\s*lift\s*>\s*0?\.2\s*\)/,
				`// Glow is a light floor. It must not turn clear pixels solid.
\t// Full glow uses the image colors without diffuse shading or scene light.
\tvec3 illumination = G3D_GLOW >= 1.0 ? vec3(1.0) : max(vec3(light) * LIGHTCOLOR, vec3(G3D_GLOW));
\tgl_FragColor = vec4(vec3(lift) + color.rgb * illumination, color.a);

\tif (lift > 0.2)`,
			);
		material.customProgramCacheKey = function () {
			return (state.customProgramCacheKey?.call(this) || '') + ':pswg_g3d_surface_v2';
		};
		previewMaterials.set(material, state);
		material.needsUpdate = true;
		return state;
	}

	/** Remove our shader hook before a texture returns to another format. */
	function restorePreviewMaterial(material) {
		const state = previewMaterials.get(material);
		if (!state) return;
		Object.assign(material, {
			fragmentShader: state.fragmentShader,
			customProgramCacheKey: state.customProgramCacheKey,
			transparent: state.transparent,
			depthWrite: state.depthWrite,
			side: state.side,
			blending: state.blending,
			needsUpdate: true,
		});
		delete material.uniforms.G3D_SURFACE;
		delete material.uniforms.G3D_GLOW;
		previewMaterials.delete(material);
	}

	/** Show the saved settings, or the unsaved values in an open texture dialog. */
	function updateTexturePreview(texture) {
		const material = texture.getOwnMaterial();
		if (!isG3dProject()) {
			restorePreviewMaterial(material);
			return;
		}
		ensureTextureSettings(texture);
		const settings = previewSettings.get(texture) || texture;
		const target = settings.g3d_preview_target;
		const layer = settings.g3d_layers[target];
		const emissive = target === 'entity' && layer === 'minecraft:entity/translucent_emissive';
		const translucent = layer === 'minecraft:' + target + '/translucent' || emissive;
		const solid = layer === 'minecraft:' + target + '/solid';
		const noCull = (target === 'entity' && layer === 'minecraft:entity/cutout_no_cull') || emissive;
		const state = installPreviewShader(texture, material);
		state.uniforms.G3D_SURFACE.value = solid ? 0 : translucent ? 2 : 1;
		state.uniforms.G3D_GLOW.value = emissive ? 1 : settings.g3d_light_emission / 15;
		material.uniforms.EMISSIVE.value = false;
		material.side = settings.g3d_double_sided || noCull ? THREE.DoubleSide : THREE.FrontSide;
		material.blending = THREE.NormalBlending;
		if (material.transparent !== translucent || material.depthWrite === translucent) {
			material.transparent = translucent;
			material.depthWrite = !translucent;
			material.needsUpdate = true;
		}
	}

	/** Read artist controls while preserving custom layers and the material ID. */
	function settingsFromForm(result) {
		return {
			g3d_texture: result.game_texture,
			g3d_layers:
				result.surface_style === 'custom'
					? {block: result.block_layer, item: result.item_layer, entity: result.entity_layer}
					: layersForStyle(result.surface_style),
			g3d_light_emission: result.glow,
			g3d_double_sided: result.double_sided,
			g3d_tint_index: result.tint_index,
			g3d_preview_target: result.preview_target,
			g3d_settings_ready: true,
		};
	}

	/** Add surface controls before Blockbench builds its normal texture dialog. */
	function extendTextureDialog(dialog, texture) {
		const originalConfirm = dialog.onConfirm;
		const originalCancel = dialog.onCancel;
		const originalChange = dialog.onFormChange;
		const style = styleForTexture(texture);
		const form = {};
		for (const [key, input] of Object.entries(dialog.form_config)) {
			if (['variable', 'folder', 'namespace', 'render_mode'].includes(key)) continue;
			form[key] = input;
			if (key !== 'render_options') continue;
			Object.assign(form, {
				surface_style: {
					label: 'Surface',
					type: 'select',
					value: style,
					options: {...SURFACE_STYLES, custom: style === 'custom' && SURFACE_STYLES.custom},
					description: 'Choose how this texture looks. All faces that use it share these settings.',
				},
				glow: {
					label: 'Glow strength',
					type: 'range',
					value: texture.g3d_light_emission,
					min: 0,
					max: 15,
					step: 1,
					force_step: true,
					editable_range_label: true,
					description:
						'0 uses scene light. 15 stays fully bright. This lights the surface, not nearby blocks.',
				},
				double_sided: {
					label: 'Show both sides',
					type: 'checkbox',
					value: texture.g3d_double_sided,
					description: 'Show the back of each face too. Useful for thin sheets and fins.',
				},
			});
		}
		Object.assign(form, {
			g3d_advanced_separator: '_',
			advanced: {label: 'Advanced settings', type: 'checkbox', value: false},
			game_texture: {
				label: 'Game texture',
				value: texture.g3d_texture,
				condition: (result) => result.advanced,
				description:
					'Set from the imported file. Change this only to use another game image or a Ptex surface.',
			},
			tint_index: {
				label: 'Game tint slot',
				type: 'number',
				value: texture.g3d_tint_index,
				min: -1,
				max: 255,
				step: 1,
				force_step: true,
				condition: (result) => result.advanced,
				description:
					'-1 keeps the image colors. Other slots get a tint from the game; the editor shows the image colors.',
			},
			preview_target: {
				label: 'Preview as',
				type: 'select',
				value: texture.g3d_preview_target,
				options: {item: 'Item', block: 'Block', entity: 'Entity'},
				condition: (result) => result.advanced,
				description: 'Choose which imported surface settings to show. This changes only the editor preview.',
			},
		});
		for (const target of Object.keys(DEFAULT_LAYERS)) {
			form[target + '_layer'] = {
				label: target[0].toUpperCase() + target.slice(1) + ' layer',
				value: texture.g3d_layers[target],
				condition: (result) => result.advanced && result.surface_style === 'custom',
				description: 'Keep the imported game layer, or enter a layer supplied by your content module.',
			};
		}
		dialog.form_config = form;
		let previousStyle = style;
		textureDialogs.add(dialog);
		dialog.onFormChange = function (result) {
			if (result.surface_style !== previousStyle) {
				if (result.surface_style !== 'custom') {
					result.glow = result.surface_style.startsWith('emissive') ? 15 : 0;
					this.setFormValues({glow: result.glow}, false);
				}
				previousStyle = result.surface_style;
			}
			previewSettings.set(texture, settingsFromForm(result));
			texture.updateMaterial();
			originalChange?.call(this, result);
		};
		dialog.onConfirm = function (result, event) {
			const settings = settingsFromForm(result);
			try {
				normalizeMaterial({
					id: texture.g3d_material_id,
					texture: settings.g3d_texture || textureResourceId(texture),
					layers: settings.g3d_layers,
					lightEmission: settings.g3d_light_emission,
					doubleSided: settings.g3d_double_sided,
					tintIndex: settings.g3d_tint_index,
				});
			} catch (error) {
				reportError('Check the texture settings', error);
				return false;
			}
			// The init_edit event applies our data after the native undo snapshot.
			// Blockbench then saves the metadata and our settings in one undo step.
			pendingSettings.set(texture, settings);
			try {
				return originalConfirm.call(this, result, event);
			} finally {
				pendingSettings.delete(texture);
				previewSettings.delete(texture);
				textureDialogs.delete(this);
				texture.updateMaterial();
			}
		};
		dialog.onCancel = function (event) {
			previewSettings.delete(texture);
			textureDialogs.delete(this);
			texture.updateMaterial();
			return originalCancel?.call(this, event);
		};
	}

	/** Use the native texture context menu for quick, undoable surface choices. */
	function surfaceMenu(texture) {
		ensureTextureSettings(texture);
		const currentStyle = styleForTexture(texture);
		return Object.entries(SURFACE_STYLES)
			.filter(([style]) => style !== 'custom')
			.map(([style, name]) => ({
				name,
				icon: currentStyle === style ? 'far.fa-dot-circle' : 'far.fa-circle',
				click() {
					const selected = Texture.all.filter(
						(candidate) => candidate === texture || candidate.multi_selected,
					);
					Undo.initEdit({textures: selected});
					for (const candidate of selected) {
						ensureTextureSettings(candidate);
						candidate.g3d_layers = layersForStyle(style);
						candidate.g3d_light_emission = style.startsWith('emissive') ? 15 : 0;
						candidate.updateMaterial();
					}
					Undo.finishEdit('Change texture surface');
				},
			}));
	}

	/** Attach the small adapters that let native texture tools keep working. */
	function registerTextureWorkflow() {
		patchMethod(
			Texture.prototype,
			'fromPath',
			(original) =>
				function (imagePath, ...args) {
					if (!isG3dProject()) return original.call(this, imagePath, ...args);
					const result = withDistinctTexturePaths(this, imagePath, () =>
						original.call(this, imagePath, ...args),
					);
					// Change File points at a new game image. A display-name edit does not.
					this.g3d_texture = '';
					ensureTextureSettings(this);
					return result;
				},
		);
		patchMethod(
			Texture.prototype,
			'add',
			(original) =>
				function (...args) {
					if (!isG3dProject()) return original.apply(this, args);
					ensureTextureSettings(this);
					ensureTextureMaterialId(this);
					ensureTextureDisplayName(this);
					return withDistinctTexturePaths(this, this.path, () => original.apply(this, args));
				},
		);
		patchMethod(
			Texture.prototype,
			'remove',
			(original) =>
				function (...args) {
					const result = original.apply(this, args);
					restorePreviewMaterial(this.getOwnMaterial());
					previewSettings.delete(this);
					return result;
				},
		);
		patchMethod(
			Texture.prototype,
			'updateMaterial',
			(original) =>
				function (...args) {
					// Restore first so a different format's new choices win this update.
					if (!isG3dProject()) restorePreviewMaterial(this.getOwnMaterial());
					const result = original.apply(this, args);
					updateTexturePreview(this);
					return result;
				},
		);
		patchMethod(
			Texture.prototype,
			'propertiesDialog',
			(original) =>
				function (...args) {
					if (!isG3dProject()) return original.apply(this, args);
					ensureTextureSettings(this);
					ensureTextureMaterialId(this);
					const previous = editingTexture;
					editingTexture = this;
					try {
						return original.apply(this, args);
					} finally {
						editingTexture = previous;
					}
				},
		);
		patchMethod(
			Dialog.prototype,
			'build',
			(original) =>
				function (...args) {
					// The native dialog does not read Property.inputs. Extend its form here,
					// before InputForm builds it, and only inside propertiesDialog's call.
					if (editingTexture && this.id === 'texture_edit' && !textureDialogs.has(this)) {
						extendTextureDialog(this, editingTexture);
					}
					return original.apply(this, args);
				},
		);
		const nativeMenu = Texture.prototype.menu.structure.find(
			(entry) => entry?.name === 'menu.texture.render_mode',
		);
		if (nativeMenu) {
			patchMethod(
				nativeMenu,
				'children',
				(original) =>
					function (texture) {
						return isG3dProject() ? surfaceMenu(texture) : original.call(this, texture);
					},
			);
		}
		listen(Blockbench, 'init_edit', ({aspects}) => {
			for (const texture of aspects.textures || []) {
				const settings = pendingSettings.get(texture);
				if (settings) Object.assign(texture, settings);
			}
		});
		const refresh = () => {
			if (!isG3dProject()) return;
			migrateLegacyMaterials();
			if (
				!Object.keys(Project.g3d_model_metadata || {}).length &&
				!Object.keys(Project.display_settings || {}).length
			) {
				// Earlier working copies did not save display data. Recover it from
				// their remembered source path before the artist exports a new copy.
				const model = readLocalSidecar(Project.export_path);
				if (Object.keys(model).length) importModelMetadata({model}, Project.export_path);
			}
			for (const texture of Texture.all) texture.updateMaterial();
		};
		listen(Blockbench, 'select_project', refresh);
		listen(Blockbench, 'convert_format', refresh);
		listen(Blockbench, 'close_project', ({project}) => {
			for (const texture of project.textures) restorePreviewMaterial(texture.getOwnMaterial());
		});
		if (Codecs.project) listen(Codecs.project, 'parsed', refresh);
		patchMethod(
			Canvas,
			'updateRenderSides',
			(original) =>
				function (...args) {
					const result = original.apply(this, args);
					if (isG3dProject()) {
						for (const texture of Texture.all) updateTexturePreview(texture);
					}
					return result;
				},
		);
		refresh();
	}

	/** Enable the native Java display controls and match the game's model origin. */
	function registerDisplayWorkflow() {
		for (const [name, slot] of [
			['gui_light', 'gui'],
			['shelf_alignment', 'on_shelf'],
		]) {
			const control = BarItems[name];
			if (!control) continue;
			patchMethod(
				control,
				'condition',
				(original) =>
					function (...args) {
						return isG3dProject()
							? Modes.display && DisplayMode.display_slot === slot
							: original.apply(this, args);
					},
			);
		}
		patchMethod(
			DisplayMode,
			'load',
			(original) =>
				function (...args) {
					const result = original.apply(this, args);
					if (isG3dProject() && Project.model_3d) {
						// Vanilla subtracts half a block on all axes before drawing an item.
						// The normal centered editing grid must not remove the X/Z offset.
						Project.model_3d.position.set(-8, -8, -8);
					}
					return result;
				},
		);
	}

	// Plugin setup and cleanup

	/** Register saved metadata and the small set of useful group/locator controls. */
	function registerProperties() {
		properties.push(
			new Property(ModelProject, 'object', 'g3d_model_metadata', {
				default: () => ({}),
				condition: {formats: [FORMAT_ID]},
				exposed: false,
			}),
		);
		properties.push(
			new Property(ModelProject, 'boolean', 'shelf_align_bottom', {
				default: true,
				condition: {formats: [FORMAT_ID]},
				exposed: false,
			}),
		);
		// These hidden properties let native save, copy, and undo retain our data.
		for (const [name, value] of Object.entries(TEXTURE_DEFAULTS)) {
			const type = typeof value === 'object' ? 'object' : typeof value;
			properties.push(
				new Property(Texture, type, name, {
					default: typeof value === 'object' ? () => cloneJson(value) : value,
					condition: {formats: [FORMAT_ID]},
					exposed: false,
				}),
			);
		}
		// Keep the old fields only so earlier .bbmodel projects can be upgraded.
		properties.push(
			new Property(ModelProject, 'array', 'g3d_materials', {
				default: () => [],
				condition: {formats: [FORMAT_ID]},
				exposed: false,
			}),
		);
		properties.push(
			new Property(Group, 'string', 'g3d_node_id', {
				condition: {formats: [FORMAT_ID]},
				exposed: false,
			}),
		);
		properties.push(
			new Property(Group, 'vector', 'g3d_scale', {
				default: [1, 1, 1],
				condition: {formats: [FORMAT_ID]},
				inputs: {
					element_panel: {
						input: {label: 'Scale', type: 'vector'},
						onChange(value, groups) {
							groups.forEach((group) => group.preview_controller.updateTransform(group));
							Canvas.updateAllBones();
						},
					},
				},
			}),
		);
		properties.push(
			new Property(Mesh, 'string', 'g3d_mesh_id', {
				condition: {formats: [FORMAT_ID]},
				exposed: false,
			}),
		);
		properties.push(
			new Property(Mesh, 'string', 'g3d_material_id', {
				condition: {formats: [FORMAT_ID]},
				exposed: false,
			}),
		);
		properties.push(
			new Property(Mesh, 'object', 'g3d_vertex_source', {
				default: () => ({}),
				condition: {formats: [FORMAT_ID]},
				exposed: false,
			}),
		);
		properties.push(
			new Property(Locator, 'string', 'g3d_socket_id', {
				condition: {formats: [FORMAT_ID]},
				exposed: false,
			}),
		);
		properties.push(
			new Property(Locator, 'vector', 'g3d_scale', {
				default: [1, 1, 1],
				condition: {formats: [FORMAT_ID]},
				inputs: {
					element_panel: {
						input: {label: 'Scale', type: 'vector'},
						onChange(value, locators) {
							locators.forEach((locator) => locator.preview_controller.updateTransform(locator));
						},
					},
				},
			}),
		);
	}

	/** Register the source codec and enable the native tools this format supports. */
	function registerFormat() {
		codec = new Codec(CODEC_ID, {
			name: 'G3D V1 Source',
			extension: 'jg3d',
			remember: true,
			load_filter: {
				type: 'json',
				extensions: ['jg3d'],
				condition(source) {
					return (
						source &&
						source.version === 1 &&
						Array.isArray(source.materials) &&
						Array.isArray(source.nodes) &&
						Array.isArray(source.meshes)
					);
				},
			},
			parse(source, sourcePath) {
				try {
					importSource(source, sourcePath);
				} catch (error) {
					reportError('Could not open G3D source', error);
				}
			},
			compile() {
				try {
					return JSON.stringify(compileSource(), null, '\t');
				} catch (error) {
					reportError('Could not export G3D source', error);
					throw error;
				}
			},
		});

		format = new ModelFormat({
			id: FORMAT_ID,
			name: 'PSWG G3D',
			icon: 'icon-format_java',
			category: 'minecraft',
			target: 'Minecraft: Java Edition — PSWG G3D',
			extension: 'jg3d',
			show_on_start_screen: true,
			box_uv: false,
			optional_box_uv: true,
			single_texture: false,
			single_texture_default: false,
			bone_rig: true,
			centered_grid: true,
			meshes: true,
			locators: true,
			texture_folder: true,
			texture_meshes: false,
			display_mode: true,
			vertex_color_ambient_occlusion: true,
			select_texture_for_particles: true,
			animation_mode: false,
			pose_mode: false,
			model_identifier: false,
			euler_order: 'XYZ',
			node_name_regex: '\\w.-/',
			codec,
			onSetup(project, newModel) {
				if (!newModel) return;
				const root = new Group({
					name: 'root',
					g3d_node_id: 'root',
					origin: [0, 0, 0],
					g3d_scale: [1, 1, 1],
				});
				root.addTo('root').init();
			},
		});
		codec.format = format;

		for (const method of ['setup', 'updateTransform']) {
			patchMethod(
				Group.preview_controller,
				method,
				(original) =>
					function (group) {
						const result = original.call(this, group);
						applyGroupScale(group);
						return result;
					},
			);
		}
	}

	/** Add source group scale after the native controller updates its transform. */
	function applyGroupScale(group) {
		if (isG3dProject() && group.mesh) {
			const scale = group.g3d_scale || [1, 1, 1];
			group.mesh.scale.set(scale[0], scale[1], scale[2]);
			group.mesh.updateMatrixWorld();
		}
	}

	/** Put model export in the normal File menu; texture editing stays native. */
	function registerActions() {
		const exportAction = new Action('pswg_g3d_export_source', {
			name: 'Export G3D Model',
			icon: 'icon-format_java',
			category: 'file',
			condition: isG3dProject,
			click: () => codec.export(),
		});
		codec.export_action = exportAction;
		MenuBar.addAction(exportAction, MENU_PATHS.export);
		actions.push({action: exportAction, path: MENU_PATHS.export + '.' + exportAction.id});
	}

	Plugin.register('pswg_g3d', {
		title: 'PSWG G3D Model Format',
		author: 'parzi',
		icon: 'icon-format_java',
		description: 'Import, edit, and export PSWG G3D .jg3d source models.',
		version: '0.1.0',
		min_version: '5.2.0',
		variant: 'both',
		/** Load the format first, then attach its native-editor adapters. */
		onload() {
			registerProperties();
			registerFormat();
			registerActions();
			registerTextureWorkflow();
			registerDisplayWorkflow();
		},
		/** Cancel live edits and restore native methods before removing the format. */
		onunload() {
			for (const dialog of textureDialogs) dialog.cancel();
			for (const cleanup of cleanups.splice(0).reverse()) cleanup();
			for (const material of Array.from(previewMaterials.keys())) {
				const texture = previewMaterials.get(material).texture;
				restorePreviewMaterial(material);
				texture.updateMaterial();
			}
			for (const entry of actions) {
				MenuBar.removeAction(entry.path);
				entry.action.delete();
			}
			actions = [];
			if (format) format.delete();
			if (codec) codec.delete();
			for (const property of properties) property.delete();
			properties = [];
			previewSettings.clear();
			pendingSettings.clear();
			textureDialogs.clear();
			editingTexture = null;
			format = null;
			codec = null;
		},
	});
})();
