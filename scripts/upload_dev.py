import os
import subprocess
import sys

import requests

# Ensure working directory is correct
os.chdir(sys.path[0])

print("Current directory: " + os.getcwd())

gittags = subprocess.check_output("git describe --tags").strip()
file = f"pswg-{gittags}.jar"
if os.path.isfile(file):
    with open(file, "rb") as fp:
        with requests.post(sys.argv[1], files={"file": fp}, data={"content": f"Build {os.environ['BUILD_NUMBER']} built! <:mc_cake:711406798292254792>"}) as resp:
            print("discord response: ", resp.text)
        subprocess.check_call(["curl", "--location", "--resolve=kb1000.de:443:10.244.44.214", "--request", "POST", sys.argv[2], "--form", f"file=@{file}"])
else:
    with requests.post(sys.argv[1], data={"content": f"Build {os.environ['BUILD_NUMBER']} failed! \N{CROSS MARK}"}) as resp:
        print("discord response: ", resp.text)
