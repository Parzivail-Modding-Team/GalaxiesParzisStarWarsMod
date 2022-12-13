bl_info = {
    "name": "Snap This Vertex",
    "blender": (2, 80, 0),
    "category": "UV",
}

import bpy
import bmesh

class SnapThisPixel(bpy.types.Operator):
    """Moves an entire island to align one pixel to the pixel grid"""
    bl_idname = "uv.snap_this_pixel"
    bl_label = "Snap This Pixel"
    bl_options = {'REGISTER', 'UNDO'}

    def execute(self, context):

        for obj in context.selected_objects:
            me = obj.data
            bm = bmesh.from_edit_mesh(me)

            uv_layer = bm.loops.layers.uv.verify()
            
            for face in bm.faces:
                for loop in face.loops:
                    uv = loop[uv_layer]
                    if uv.select:
                    
                        originalPos = uv.uv.to_3d()
                        bpy.ops.uv.snap_selected(target='PIXELS')
                        newPos = uv.uv.to_3d()
                        
                        delta = newPos - originalPos
                        bpy.ops.uv.select_linked()
                        uv.select = False
                        bpy.ops.transform.translate(value=delta)
                
                        bmesh.update_edit_mesh(me)

                        return {'FINISHED'}
        
        return {'PASS_THROUGH'}

def menu_func(self, context):
    self.layout.operator(SnapThisPixel.bl_idname)

def register():
    bpy.utils.register_class(SnapThisPixel)
    bpy.types.IMAGE_MT_uvs.append(menu_func)

def unregister():
    bpy.utils.unregister_class(SnapThisPixel)

if __name__ == "__main__":
    register()