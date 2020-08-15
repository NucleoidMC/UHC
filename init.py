#!/usr/bin/env python3

import os
from os import path
from pathlib import Path
import shutil


def process_file(file_path: Path, transform_text, is_java: bool = False):
    f = open(str(file_path), "r")
    contents = transform_text(f.read())
    f.close()

    os.remove(str(file_path))

    # strip out old package (src/main/java/org/example/MODNAME)
    file_path = path.join(package_root, *file_path.parts[6:]) if is_java else file_path
    file_path = str(file_path).replace("MODCLASS", mod_class).replace("MODNAME", mod_id)

    f = open(file_path, "w+")
    f.write(contents)
    f.close()


def transform_java(contents: str) -> str:
    contents = contents.replace("org.example.MODNAME", package) \
        .replace("MODNAME", mod_id) \
        .replace("MODCLASS", mod_class)
    return contents


def transform_fabric_mod_json(contents: str) -> str:
    contents = contents \
        .replace("MODNAME_PRETTY", mod_name_pretty)\
        .replace("MODNAME", mod_id) \
        .replace("MODCLASS", mod_class) \
        .replace("PACKAGE", package) \
        .replace("AUTHOR", author) \
        .replace("LICENSE", mod_license) \
        .replace("DESCRIPTION", description)
    return contents


def transform_gradle_properties(contents: str) -> str:
    contents = contents.replace("MODNAME", mod_id).replace("MAVEN_GROUP", maven_group)
    return contents


def get_attribute(prompt: str, default: str = None) -> str:
    attr = input(prompt).strip()
    while not attr and not default:
        print("  Error: input required")
        attr = input(prompt).strip()

    return attr if attr else default


mod_name_pretty = get_attribute("Mod name (e.g Loop-de-loop): ")
mod_id = get_attribute("Mod id (e.g loopdeloop): ")
mod_class = get_attribute("Mod class (e.g LoopDeLoop): ")
maven_group = get_attribute("Maven group (e.g io.github.restioson): ")
package = get_attribute(
    "Package (e.g io.github.restioson.loopdeloop. Default = {maven_group}.{mod_id}): ",
    f"{maven_group}.{mod_id}",
)
author = get_attribute("Author name: ")
mod_license = get_attribute("License (e.g LGPLv3, MIT, Apache2): ")
description = get_attribute("Mod description: ")

java_root = path.join("src", "main", "java")
package_root = path.join(java_root, *package.split("."))
res = path.join("src", "main", "resources")
lowest_dir = path.join(package_root, "game", "map")
lowest_dir2 = path.join(res, "data", mod_id, "games")

os.makedirs(lowest_dir)
os.makedirs(lowest_dir2)


def walk(target_path, level=0):
    files = []

    for file in target_path.iterdir():
        if file.is_dir():
            files.extend(walk(file, level + 1))
        else:
            files.append(file)

    return files


for java_file in walk(Path(java_root)):
    process_file(java_file, transform_java, is_java=True)

template_resources = path.join(res, "data", "MODNAME")
std_path = path.join(template_resources, "games")
process_file(
    Path(path.join(std_path, "standard.json")),
    lambda contents: contents.replace("MODNAME", mod_id),
)
process_file(Path(path.join(res, "fabric.mod.json")), transform_fabric_mod_json)
process_file(Path("gradle.properties"), transform_gradle_properties)

org_example = path.join(java_root, "org", "example")
os.remove("README.md")
shutil.rmtree(path.join(std_path))
os.removedirs(template_resources)
shutil.rmtree(path.join(org_example, "MODNAME"))
os.removedirs(org_example)

print("Your mod has been set up! It is recommended to delete `.git` and init.py, and then initialise a new git "
      "repository, including your chosen license. You can then import the project into your chosen IDE.")
