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

for ore in (
        "beskar",
        "chromium",
        "cortosis",
        "desh",
        "diatium",
        "lommite",
        "titanium",
):
    items[f"{ore}_ore"] = {
        "output": f"{ore}_ingot",
        "smoking_time": 0,
        "campfire_cooking_time": 0,
        "blasting_time": 100,
        "xp": "1",
    }

for input, meta in items.items():
    xp = meta.get("xp", "0.35")

    smelting_time = meta.get("smelting_time", 200)
    if smelting_time:
        with open(directory / f"{meta['output']}.json", "w", encoding="utf-8", newline="\n") as fp:
            fp.write(f"""\
{{
\t"type": "minecraft:smelting",
\t"ingredient": {{
\t\t"item": "pswg:{input}"
\t}},
\t"result": "pswg:{meta['output']}",
\t"experience": {xp},
\t"cookingtime": {smelting_time}
}}
""")
    campfire_cooking_time = meta.get("campfire_cooking_time", 600)
    if campfire_cooking_time:
        with open(directory / f"{meta['output']}_from_campfire_cooking.json", "w", encoding="utf-8",
                  newline="\n") as fp:
            fp.write(f"""\
{{
\t"type": "minecraft:campfire_cooking",
\t"ingredient": {{
\t\t"item": "pswg:{input}"
\t}},
\t"result": "pswg:{meta['output']}",
\t"experience": {xp},
\t"cookingtime": {campfire_cooking_time}
}}
""")
    smoking_time = meta.get("smoking_time", 100)
    if smoking_time:
        with open(directory / f"{meta['output']}_from_smoking.json", "w", encoding="utf-8", newline="\n") as fp:
            fp.write(f"""\
{{
\t"type": "minecraft:smoking",
\t"ingredient": {{
\t\t"item": "pswg:{input}"
\t}},
\t"result": "pswg:{meta['output']}",
\t"experience": {xp},
\t"cookingtime": {smoking_time}
}}
""")

    blasting_time = meta.get("blasting_time")
    if blasting_time:
        with open(directory / f"{meta['output']}_from_blasting.json", "w", encoding="utf-8", newline="\n") as fp:
            fp.write(f"""\
{{
\t"type": "minecraft:blasting",
\t"ingredient": {{
\t\t"item": "pswg:{input}"
\t}},
\t"result": "pswg:{meta['output']}",
\t"experience": {xp},
\t"cookingtime": {blasting_time}
}}
""")
