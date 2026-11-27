htmx.defineExtension("push-url-w-params", {
    init: function(api) {
        document.addEventListener("htmx:configRequest", function(e) {
            const path = e.target.getAttribute('data-push-url')
            const params = new URLSearchParams(e.detail.parameters).toString()
            const url = `${window.location.origin}${path}?${params}`
            window.history.pushState({}, '', url);
        })
    },
})