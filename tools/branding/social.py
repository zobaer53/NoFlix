"""Builds the 1280x640 social preview (GitHub repo card and og:image)."""
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter, ImageFont

import render

ROOT = str(Path(__file__).resolve().parents[2])
FONT = f"{ROOT}/app/src/main/res/font/montserrat_%s.ttf"
W, H = 1280, 640


def font(weight, size):
    return ImageFont.truetype(FONT % weight, size)


def rounded_mask(size, radius):
    w, h = size
    m = Image.new("L", (w * 4, h * 4), 0)
    ImageDraw.Draw(m).rounded_rectangle((0, 0, w * 4 - 1, h * 4 - 1), radius=radius * 4, fill=255)
    return m.resize(size, Image.LANCZOS)


def phone(path, height):
    """Screenshot (status bar cropped) in a dark rounded phone frame."""
    shot = Image.open(path).convert("RGB")
    shot = shot.crop((0, 120, shot.width, shot.height))
    sw = round(shot.width * height / shot.height)
    shot = shot.resize((sw, height), Image.LANCZOS)
    bezel = 10
    fw, fh = sw + bezel * 2, height + bezel * 2
    frame = Image.new("RGBA", (fw, fh), (0, 0, 0, 0))
    body = Image.new("RGBA", (fw, fh), (58, 54, 80, 255))
    frame.paste(body, (0, 0), rounded_mask((fw, fh), 34))
    frame.paste(shot, (bezel, bezel), rounded_mask((sw, height), 26))
    return frame


def shadow(img, blur=22, alpha=150):
    a = img.split()[3].point(lambda v: v * alpha // 255)
    s = Image.new("RGBA", img.size, (0, 0, 0, 0))
    s.putalpha(a)
    pad = blur * 3
    out = Image.new("RGBA", (img.width + pad * 2, img.height + pad * 2), (0, 0, 0, 0))
    out.paste(s, (pad, pad))
    return out.filter(ImageFilter.GaussianBlur(blur)), pad


def background():
    big = render.render(render.BACKGROUND, 1280)
    return big.crop((0, 320, 1280, 960)).convert("RGBA")


def chip(img, x, y, text):
    f = font("medium", 22)
    overlay = Image.new("RGBA", img.size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)
    tw = draw.textlength(text, font=f)
    draw.rounded_rectangle((x, y, x + tw + 36, y + 44), radius=22,
                           fill=(18, 205, 217, 38), outline=(18, 205, 217, 140), width=2)
    img.alpha_composite(overlay)
    ImageDraw.Draw(img).text((x + 18, y + 22), text, font=f, fill=(235, 235, 239), anchor="lm")
    return x + tw + 36 + 14


def social_preview():
    img = background()

    # Phones on the right, the back one slightly lower.
    for path, x, y in ((f"{ROOT}/screenshot-4-details.png", 966, 104),
                       (f"{ROOT}/screenshot-1-home.png", 770, 56)):
        p = phone(path, 500)
        s, pad = shadow(p)
        img.alpha_composite(s, (x - pad, y - pad + 12))
        img.alpha_composite(p, (x, y))

    icon = render.masked(112, 0.24)
    s, pad = shadow(icon, blur=14, alpha=120)
    img.alpha_composite(s, (80 - pad, 66 - pad + 6))
    img.alpha_composite(icon, (80, 66))

    d = ImageDraw.Draw(img)
    d.text((80, 282), "NoFlix", font=font("semibold", 92), fill=(255, 251, 255), anchor="ls")
    d.text((80, 346), "Watch movies & TV series", font=font("semibold", 40), fill=(255, 251, 255), anchor="ls")
    d.text((80, 398), "ad-free on Android", font=font("semibold", 40), fill=(18, 205, 217), anchor="ls")
    d.text((80, 450), "Free & open source  ·  no sign-up  ·  no trackers",
           font=font("regular", 22), fill=(146, 146, 157), anchor="ls")
    x = 80
    for t in ("Built-in ad blocker", "Full-screen player", "Watchlist"):
        x = chip(img, x, 494, t)
    return img.convert("RGB")
