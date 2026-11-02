"""Create small WebP copies of the site images (originals are never touched).
Run from the folder that contains index.html and assets/:
    pip install pillow
    python make-thumbs.py
"""
from pathlib import Path
from PIL import Image, ImageOps

JOBS = [  # (folder, output width in px, webp quality)
    ("assets/screenshots", 600, 80),         # shown at ~220-340px wide
    ("assets/promotional-slides", 1000, 80), # shown at ~520-720px wide
]
for folder, width, quality in JOBS:
    src = Path(folder)
    out = src / "thumbs"
    out.mkdir(exist_ok=True)
    for f in sorted(src.iterdir()):
        if f.suffix.lower() not in (".png", ".jpg", ".jpeg"):
            continue
        im = ImageOps.exif_transpose(Image.open(f)).convert("RGB")
        if im.width > width:
            im = im.resize((width, round(im.height * width / im.width)), Image.LANCZOS)
        dest = out / (f.stem + ".webp")
        im.save(dest, "WEBP", quality=quality, method=6)
        print(f"{f.name}: {f.stat().st_size//1024} KB -> {dest.stat().st_size//1024} KB")
