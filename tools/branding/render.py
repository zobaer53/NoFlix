"""Draws the NoFlix launcher icon on the 108x108 adaptive-icon canvas.

The shapes mirror app/src/main/res/drawable/zed_movies_round_*.xml; change both together.
"""
import io
import cairosvg
from PIL import Image, ImageDraw

SHIELD = ("M50.6,27.6 Q54,26.4 57.4,27.6 L73.6,33.4 Q76,34.3 76,36.8 L76,54 "
          "C76,67.5 66.5,76.8 55.6,81.4 Q54,82.1 52.4,81.4 "
          "C41.5,76.8 32,67.5 32,54 L32,36.8 Q32,34.3 34.4,33.4 Z")
SHIELD_LEFT = ("M54,26.7 Q52.2,26.8 50.6,27.6 L34.4,33.4 Q32,34.3 32,36.8 L32,54 "
               "C32,67.5 41.5,76.8 52.4,81.4 Q53.2,81.75 54,81.75 Z")
PLAY = "M49,44 L49,64 L66,54 Z"

BACKGROUND = """
  <defs>
    <linearGradient id="bg" x1="0" y1="0" x2="108" y2="108" gradientUnits="userSpaceOnUse">
      <stop offset="0" stop-color="#2E2A47"/><stop offset="1" stop-color="#14121D"/>
    </linearGradient>
    <radialGradient id="glow" cx="54" cy="50" r="46" gradientUnits="userSpaceOnUse">
      <stop offset="0" stop-color="#6647F0" stop-opacity="0.45"/>
      <stop offset="1" stop-color="#6647F0" stop-opacity="0"/>
    </radialGradient>
  </defs>
  <rect width="108" height="108" fill="url(#bg)"/>
  <rect width="108" height="108" fill="url(#glow)"/>
"""

FOREGROUND = f"""<g transform="translate(54,54) scale(0.88) translate(-54,-54)">
  <defs>
    <linearGradient id="shield" x1="34" y1="28" x2="74" y2="80" gradientUnits="userSpaceOnUse">
      <stop offset="0" stop-color="#12CDD9"/><stop offset="1" stop-color="#6647F0"/>
    </linearGradient>
  </defs>
  <path d="{SHIELD}" fill="#000000" fill-opacity="0.28" transform="translate(0,2.2)"/>
  <path d="{SHIELD}" fill="url(#shield)"/>
  <path d="{SHIELD_LEFT}" fill="#FFFFFF" fill-opacity="0.12"/>
  <path d="{PLAY}" fill="#FFFFFF" stroke="#FFFFFF" stroke-width="4" stroke-linejoin="round"/>
</g>"""


def svg(body):
    return (f'<svg xmlns="http://www.w3.org/2000/svg" width="108" height="108" '
            f'viewBox="0 0 108 108">{body}</svg>')


def render(body, size):
    png = cairosvg.svg2png(bytestring=svg(body).encode(), output_width=size, output_height=size)
    return Image.open(io.BytesIO(png)).convert("RGBA")


def full(size):
    """Background + foreground, cropped to the 72dp visible viewport like a launcher does."""
    big = int(size * 108 / 72)
    img = render(BACKGROUND + FOREGROUND, big)
    off = (big - size) // 2
    return img.crop((off, off, off + size, off + size))


def masked(size, radius_ratio):
    img = full(size)
    mask = Image.new("L", (size * 4, size * 4), 0)
    ImageDraw.Draw(mask).rounded_rectangle(
        (0, 0, size * 4 - 1, size * 4 - 1), radius=int(size * 4 * radius_ratio), fill=255)
    out = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    out.paste(img, (0, 0), mask.resize((size, size), Image.LANCZOS))
    return out
