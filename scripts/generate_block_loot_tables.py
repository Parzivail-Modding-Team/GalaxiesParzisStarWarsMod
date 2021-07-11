import pathlib

directory = pathlib.Path(
    __file__).parent.parent / "src" / "main" / "resources" / "data" / "pswg" / "loot_tables" / "blocks"

blocks = [
    "chromium_ore",
    "cortosis_ore",
    "diatium_ore",
    "exonium_ore",
    "helicite_ore",
    "ionite_ore",
    "kelerium_ore",
    "rubindum_ore",
    "thorilide_ore",
    "titanium_ore",
    "sand_tatooine",
]

for block in blocks:
    with open(directory / f"{block}.json", "w", newline="\n", encoding="utf-8") as fp:
        fp.write(f"""\
{{
  "type": "minecraft:block",
  "pools": [
    {{
      "rolls": 1,
      "entries": [
        {{
          "type": "minecraft:item",
          "name": "pswg:{block}"
        }}
      ],
      "conditions": [
        {{
        "condition": "minecraft:survives_explosion"
        }}
      ]
    }}
  ]
}}""")
