(() => {
    const appShell = document.querySelector(".app-shell");
    const sidebar = document.querySelector(".sidebar");
    const sidebarToggler = document.querySelector(".sidebar-toggler");
    // Añadimos el selector para el botón de menú móvil que está en el header principal
    const menuToggler = document.querySelector(".menu-toggler");
    const MEDIA_MOBILE = window.matchMedia("(max-width: 1024px)");
    const LS_KEY = "sidebar:collapsed";

    if (!appShell || !sidebar) return;

    // --- Helpers ---
    const isCollapsed = () => appShell.classList.contains("collapsed");

    const applyCollapsed = (collapsed) => {
        appShell.classList.toggle("collapsed", collapsed);
        localStorage.setItem(LS_KEY, collapsed ? "1" : "0");
        if (sidebarToggler) {
            sidebarToggler.setAttribute("aria-expanded", String(!collapsed));
            sidebarToggler.setAttribute("aria-label", collapsed ? "Expandir barra" : "Colapsar barra");
        }
    };

    const restoreFromStorage = () => {
        const saved = localStorage.getItem(LS_KEY);
        // Solo aplicar en vista de escritorio
        if (!MEDIA_MOBILE.matches && (saved === "1" || saved === "0")) {
            applyCollapsed(saved === "1");
        }
    };

    const toggleMobileMenu = (forceState) => {
        const shouldOpen = typeof forceState === "boolean" ? forceState : !sidebar.classList.contains("menu-active");
        sidebar.classList.toggle("menu-active", shouldOpen);

        if (menuToggler) {
            menuToggler.setAttribute("aria-expanded", String(shouldOpen));
            menuToggler.setAttribute("aria-label", shouldOpen ? "Cerrar menú" : "Abrir menú");
        }
    };

    // --- Lógica de Eventos ---

    // 1. Toggler para colapsar en escritorio
    if (sidebarToggler) {
        sidebarToggler.addEventListener("click", () => {
            if (!MEDIA_MOBILE.matches) {
                applyCollapsed(!isCollapsed());
            }
        });
    }

    // 2. Toggler para abrir/cerrar en móvil
    if (menuToggler) {
        menuToggler.addEventListener("click", () => {
            toggleMobileMenu();
        });
    }

    // 3. Cerrar menú móvil al hacer click fuera
    document.addEventListener("click", (ev) => {
        if (!MEDIA_MOBILE.matches || !sidebar.classList.contains("menu-active")) return;

        const clickedInsideSidebar = sidebar.contains(ev.target);
        const clickedToggler = menuToggler && menuToggler.contains(ev.target);

        if (!clickedInsideSidebar && !clickedToggler) {
            toggleMobileMenu(false);
        }
    });

    // 4. Cerrar con la tecla ESC
    document.addEventListener("keydown", (ev) => {
        if (MEDIA_MOBILE.matches && ev.key === "Escape" && sidebar.classList.contains("menu-active")) {
            toggleMobileMenu(false);
        }
    });

    // --- Inicialización y Responsive ---
    const handleResponsive = () => {
        if (MEDIA_MOBILE.matches) {
            appShell.classList.remove("collapsed");
            sidebar.classList.remove("menu-active");
        } else {
            // En escritorio, restauramos la preferencia y nos aseguramos de que el menú móvil no esté activo
            restoreFromStorage();
            sidebar.classList.remove("menu-active");
        }
    };

    // Carga inicial
    restoreFromStorage();
    MEDIA_MOBILE.addEventListener("change", handleResponsive);

})();