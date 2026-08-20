document.addEventListener('DOMContentLoaded', () => {
  const navbar = document.getElementById('navbar');
  const showcase = document.getElementById('showcase');
  const video = document.getElementById('mockupVideo');
  const fallback = document.getElementById('videoFallback');

  const deckItems = [
    { el: document.getElementById('item1'), type: 'text' },
    { el: document.getElementById('item2'), type: 'media' },
    { el: document.getElementById('item3'), type: 'text' },
    { el: document.getElementById('item4'), type: 'media' },
    { el: document.getElementById('item5'), type: 'text' },
    { el: document.getElementById('item6'), type: 'media' }
  ];

  if (video) {
    video.addEventListener('loadedmetadata', () => {
      video.style.display = 'block';
      if (fallback) fallback.style.display = 'none';
    });
  }

  const clamp = (val, min = 0, max = 1) => Math.min(Math.max(val, min), max);

  window.addEventListener('scroll', () => {
    const scrollY = window.scrollY;

    if (scrollY > 50) {
      navbar.classList.add('scrolled');
    } else {
      navbar.classList.remove('scrolled');
    }

    if (!showcase) return;

    const showcaseRect = showcase.getBoundingClientRect();
    const showcaseTotalHeight = showcase.offsetHeight - window.innerHeight;
    const progress = clamp(-showcaseRect.top / showcaseTotalHeight);

    // 6 Equal Brackets across 100% progress (~0.166 each)
    // Fast 3% transition in/out, long 10.6% stationary hold phase (1 full scroll step per item)
    const ranges = [
      { inStart: 0.00, holdStart: 0.03, holdEnd: 0.136, outEnd: 0.166 }, // Item 1 (Text 1)
      { inStart: 0.166, holdStart: 0.196, holdEnd: 0.302, outEnd: 0.332 }, // Item 2 (Media 1)
      { inStart: 0.332, holdStart: 0.362, holdEnd: 0.468, outEnd: 0.498 }, // Item 3 (Text 2)
      { inStart: 0.498, holdStart: 0.528, holdEnd: 0.634, outEnd: 0.664 }, // Item 4 (Media 2)
      { inStart: 0.664, holdStart: 0.694, holdEnd: 0.800, outEnd: 0.830 }, // Item 5 (Text 3)
      { inStart: 0.830, holdStart: 0.860, holdEnd: 0.970, outEnd: 1.000 }  // Item 6 (Media 3)
    ];

    deckItems.forEach((item, index) => {
      const r = ranges[index];
      const el = item.el;
      if (!el) return;

      if (progress < r.inStart || progress > r.outEnd) {
        el.style.opacity = '0';
        el.style.pointerEvents = 'none';
        if (item.type === 'text') {
          el.style.transform = 'translateY(30px) scale(0.96)';
        } else {
          el.style.transform = 'translateX(100%)';
        }
      } else {
        el.style.opacity = '1';
        el.style.pointerEvents = 'auto';

        if (item.type === 'text') {
          if (progress < r.holdStart) {
            // Animate In
            const inPhase = (progress - r.inStart) / (r.holdStart - r.inStart);
            el.style.transform = `translateY(${(30 * (1 - inPhase)).toFixed(1)}px) scale(1)`;
            el.style.opacity = inPhase.toFixed(3);
          } else if (progress >= r.holdStart && progress <= r.holdEnd) {
            // Hold Stationary
            el.style.transform = 'translateY(0px) scale(1)';
            el.style.opacity = '1';
          } else {
            // Animate Out
            const outPhase = (progress - r.holdEnd) / (r.outEnd - r.holdEnd);
            el.style.transform = `translateY(-${(20 * outPhase).toFixed(1)}px) scale(1)`;
            el.style.opacity = (1 - outPhase).toFixed(3);
          }
        } else {
          if (progress < r.holdStart) {
            // Wipe In
            const wipeIn = (progress - r.inStart) / (r.holdStart - r.inStart);
            el.style.transform = `translateX(${(100 * (1 - wipeIn)).toFixed(1)}%)`;
          } else if (progress >= r.holdStart && progress <= r.holdEnd) {
            // Hold Stationary
            el.style.transform = 'translateX(0%)';
          } else {
            // Wipe Out
            if (index === deckItems.length - 1) {
              el.style.transform = 'translateX(0%)';
            } else {
              const wipeOut = (progress - r.holdEnd) / (r.outEnd - r.holdEnd);
              el.style.transform = `translateX(-${(100 * wipeOut).toFixed(1)}%)`;
            }
          }
        }
      }
    });
  });
});