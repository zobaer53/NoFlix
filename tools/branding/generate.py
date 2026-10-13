"""Regenerates every NoFlix icon and branding image from render.py / social.py.

    python3 -m venv .venv && .venv/bin/pip install cairosvg pillow
    .venv/bin/python tools/branding/generate.py

cairosvg needs the cairo library (macOS: brew install cairo; may need
DYLD_FALLBACK_LIBRARY_PATH=/opt/homebrew/lib).
"""
from PIL import Image
import render
import social

ROOT = social.ROOT
RES = f"{ROOT}/app/src/main/res"
WEB = f"{ROOT}/docs/assets"

# Launcher icon: legacy round mipmaps (API < 26) and Play Store sources.
for density, px in {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}.items():
    render.masked(px, 0.5).save(f"{RES}/mipmap-{density}/zed_movies_round.png", optimize=True)
render.masked(512, 0).save(f"{ROOT}/app/src/main/ic_launcher-playstore.png", optimize=True)
render.masked(512, 0.5).save(f"{ROOT}/app/src/main/zed_movies_round-playstore.png", optimize=True)

# README and website.
render.masked(256, 0.22).save(f"{ROOT}/noflix-ad-free-movies-app-icon.png", optimize=True)
social.social_preview().save(f"{WEB}/social-preview.png", optimize=True)
render.masked(192, 0.24).save(f"{WEB}/icon-192.png", optimize=True)
render.masked(512, 0.24).save(f"{WEB}/icon-512.png", optimize=True)
render.masked(180, 0).save(f"{WEB}/apple-touch-icon.png", optimize=True)
render.masked(32, 0.24).save(f"{WEB}/favicon-32.png", optimize=True)
for name in ("screenshot-1-home", "screenshot-2-home", "screenshot-4-details", "screenshot-7-wishlist"):
    shot = Image.open(f"{ROOT}/{name}.png").convert("RGB")
    shot.resize((432, 960), Image.LANCZOS).save(f"{WEB}/{name}.webp", quality=80, method=6)
