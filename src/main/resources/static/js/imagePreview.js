(function () {
  const input = document.getElementById('profile_pic');
  const img = document.getElementById('profilePreview');
  const msg = document.getElementById('profileMsg');

  const DEFAULT_MSG = 'Carga un link de imagen válido.';
  const LOADING_MSG = 'Cargando vista previa...';
  const ERROR_MSG = 'No se pudo cargar la imagen. ' + DEFAULT_MSG;

  // Comprobar que se trata de un link de imagen!
  const looksLikeImageUrl = (url) => {
    if (!url) return false;
    const u = url.trim();
    if (/^data:image\//i.test(u)) return true;
    if (!/^https?:\/\//i.test(u)) return false;
    return /\.(png|jpe?g|gif|webp|bmp|svg)(\?.*)?$/i.test(u);
  };

  const tryLoadImage = (url) => new Promise((resolve, reject) => {
    const testImg = new Image();
    testImg.onload = () => resolve(true);
    testImg.onerror = () => reject(false);
    testImg.src = url;
  });

  let t;
  const debounce = (fn, delay = 250) => {
    clearTimeout(t);
    t = setTimeout(fn, delay);
  };

  const updatePreview = () => {
    const url = (input.value || '').trim();

    if (!url) {
      msg.textContent = DEFAULT_MSG;
      return;
    }

    // Mensaje de estado
    msg.textContent = LOADING_MSG;

    const quickCheck = looksLikeImageUrl(url);

    tryLoadImage(url)
      .then(() => {
        img.src = url;
        msg.textContent = quickCheck ? 'Imagen cargada con éxito' : 'Cargó, pero verifica que sea una imagen.';
      })
      .catch(() => {
        msg.textContent = ERROR_MSG;
      });
  };

  input.addEventListener('input', () => debounce(updatePreview));
  input.addEventListener('blur', updatePreview);

  if (input.value && input.value.trim()) {
    updatePreview();
  } else {
    msg.textContent = DEFAULT_MSG;
  }
})();