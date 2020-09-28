import pathlib

directory = pathlib.Path(__file__).parent.parent / "src" / "main" / "resources" / "data" / "pswg" / "recipes"

items = {
    "mynock_wing": {
        "output": "cooked_mynock_wing",
    },
    "bantha_chop": {
        "output": "cooked_bantha_chop",
    },
    "nerf_chop": {
        "output": "cooked_nerf_chop",
    },
    "gizka_chop": {
        "output": "cooked_gizka_chop",
    },
    "qrikki_bread": {
        "output": "qrikki_waffle",
        "smoking_time": 0,
    },
}

for input, meta in items.items():
    smelting_time = meta.get("smelting_time", 200)
    if smelting_time:
        with open(directory / f"{meta['output']}.json", "w", encoding="utf-8", newline="\n") as fp:
            fp.write(f"""\
{{
  "type": "minecraft:smelting",
  "ingredient": {{
    "item": "pswg:{input}"
  }},
  "result": "pswg:{meta['output']}",
  "experience": 0.35,
  "cookingtime": {smelting_time}
}}
""")
    campfire_cooking_time = meta.get("campfire_cooking_time", 600)
    if campfire_cooking_time:
        with open(directory / f"{meta['output']}_from_campfire_cooking.json", "w", encoding="utf-8", newline="\n") as fp:
            fp.write(f"""\
{{
  "type": "minecraft:campfire_cooking",
  "ingredient": {{
    "item": "pswg:{input}"
  }},
  "result": "pswg:{meta['output']}",
  "experience": 0.35,
  "cookingtime": {campfire_cooking_time}
}}
""")
    smoking_time = meta.get("smoking_time", 100)
    if smoking_time:
        with open(directory / f"{meta['output']}_from_smoking.json", "w", encoding="utf-8", newline="\n") as fp:
            fp.write(f"""\
{{
  "type": "minecraft:smoking",
  "ingredient": {{
    "item": "pswg:{input}"
  }},
  "result": "pswg:{meta['output']}",
  "experience": 0.35,
  "cookingtime": {smoking_time}
}}
""")