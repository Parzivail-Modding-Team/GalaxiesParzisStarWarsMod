import os
import subprocess
import sys

import requests

gittags = subprocess.check_output("git describe --tags").strip()
file = f"pswg-{gittags}.jar"
with open(file, "rb") as fp:
    with requests.post(sys.argv[1], files={"file": fp}, data={"content": f"Build {os.environ['BUILD_NUMBER']} built! <:mc_cake:711406798292254792>"}) as resp:
        print("discord response: ", resp.text)
    subprocess.check_call(["curl", "--location", "--resolve=kb1000.de:443:10.244.44.214", "--request", "POST", sys.argv[2], "--form", f"file=@{file}"])
