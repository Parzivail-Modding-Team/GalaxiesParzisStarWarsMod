bl_info = {
    "name": "Parzi 3D Intermediary format",
    "author": "parzivail",
    "version": (1, 0, 0),
    "blender": (2, 80, 0),
    "location": "File > Import-Export",
    "description": "Export objects, sockets, and relationships to a P3DI file",
    "warning": "",
    "wiki_url": "",
    "category": "Import-Export"}

import bpy
from bpy.props import (
        BoolProperty,
        FloatProperty,
        StringProperty,
        EnumProperty,
        )
from bpy_extras.io_utils import (
        ImportHelper,
        ExportHelper,
        orientation_helper,
        path_reference_mode,
        axis_conversion,
        )

from mathutils import Vector, Matrix
import bmesh

class ExportP3DI(bpy.types.Operator, ExportHelper):

    bl_idname = "export_scene.p3di"
    bl_label = 'Export P3DI'
    bl_options = {'PRESET'}

    filename_ext = ".p3di"
    filter_glob: StringProperty(
            default="*.p3di",
            options={'HIDDEN'},
            )

    path_mode: path_reference_mode

    check_extension = True
    
    def writeSocket(self, fw, global_matrix, o):
        """
            Writes a socket to the output stream. Sockets are transformable
            points on the model with a natural orientation.
        """
        
        parent = o.parent.name if o.parent != None else ""
        
        fw("socket \"%s\" \"%s\" (%s,%s,%s,%s)\n" % (o.name, parent, o.matrix_local[0][:], o.matrix_local[1][:], o.matrix_local[2][:], o.matrix_local[3][:]))

    def writeMesh(self, fw, global_matrix, mesh):
        me = mesh.to_mesh()

        transform = mesh.matrix_local
        if (mesh.parent == None):
            transform = global_matrix @ transform

        fw("mesh \"%s\" %s (%s,%s,%s,%s)\n" % (mesh.name, len(me.polygons), transform[0][:], transform[1][:], transform[2][:], transform[3][:]))

        if (mesh.parent != None):
            fw("parent \"%s\"\n" % mesh.parent.name)
        
        if (mesh.active_material != None):
            fw("material \"%s\"\n" % mesh.active_material.name)
        
        for i, face in enumerate(me.polygons):
            for loop_index in face.loop_indices:
                vert = me.vertices[me.loops[loop_index].vertex_index]
                uv = me.uv_layers.active.data[loop_index].uv

                v = vert.co
                n = vert.normal

                fw('v %s (%.7f, %.7f, %.7f) (%.7f, %.7f, %.7f) (%.7f, %.7f)\n' % (i, v[0], v[1], v[2], n[0], n[1], n[2], uv[0], uv[1]))

    def execute(self, context):
        scaleFactor = 1.0

        global_matrix = (Matrix.Scale(scaleFactor, 4) @
                         axis_conversion(to_forward='Z',
                                         to_up='Y',
                                         ).to_4x4())
        
        with open(self.filepath, "w", encoding="utf8", newline="\n") as f:
            fw = f.write

            for o in context.scene.objects:
                name = o.name
                transform = o.matrix_local

                parentType = o.parent_type

                if parentType != "OBJECT":
                    continue

                parentName = o.parent.name if o.parent != None else None
                type = o.type

                if type == "EMPTY":
                    if o.empty_display_type == "ARROWS":
                        self.writeSocket(fw, global_matrix, o)
                elif type == "MESH":
                    self.writeMesh(fw, global_matrix, o)

        return {'FINISHED'}


def menu_func_export(self, context):
    self.layout.operator(ExportP3DI.bl_idname, text="Parzi 3D Intermediary (.p3di)")


classes = (
    ExportP3DI,
)


def register():
    for cls in classes:
        bpy.utils.register_class(cls)

    bpy.types.TOPBAR_MT_file_export.append(menu_func_export)


def unregister():
    bpy.types.TOPBAR_MT_file_export.remove(menu_func_export)

    for cls in classes:
        bpy.utils.unregister_class(cls)


if __name__ == "__main__":
    register()
