package com.parzivail.util.jsonpipeline;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.IModel;

public class BuiltinLoader
{
	public static final ModelResourceLocation MISSING_MODEL_LOCATION = new ModelResourceLocation("builtin/missing", "missing");
	private static final String EMPTY_MODEL_RAW = "{    'elements': [        {   'from': [0, 0, 0],            'to': [16, 16, 16],            'faces': {                'down': {'uv': [0, 0, 16, 16], 'texture': '' }            }        }    ]}".replaceAll("'", "\"");
	public static final BlockbenchGeometry MODEL_GENERATED = BlockbenchGeometry.deserialize(EMPTY_MODEL_RAW);
	public static final BlockbenchGeometry MODEL_ENTITY = BlockbenchGeometry.deserialize(EMPTY_MODEL_RAW);
	private static final String MISSING_MODEL_MESH = "{    'textures': {       'particle': 'missingno',       'missingno': 'missingno'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}".replaceAll("'", "\"");
	public static final BlockbenchGeometry MODEL_MISSING = BlockbenchGeometry.deserialize(MISSING_MODEL_MESH);
	public static final IModel WRAPPED_MODEL_MISSING = new BlockbenchModel(MISSING_MODEL_LOCATION, MODEL_MISSING);

	static
	{
		MODEL_GENERATED.name = "generation marker";
		MODEL_ENTITY.name = "block entity marker";
	}
}
