// Or importing a named export, but looking at the file I doubt you want this one
//import { htmx } from "htmx.org";

console.log("adding push-url-w-params") // This runs
console.log("htmx is:" + htmx) // htmx is not null
console.log("htmx.defineExtension is:\n" + htmx.defineExtension) // htmx is not null
//htmx.extensions = {}
htmx.defineExtension("push-url-w-params", {
    init: function(api) {
        console.log("init was run") //  this runs
        document.addEventListener("htmx:configRequest", function(e) {
            console.log("configRequest was run") // this runs
            const path = e.target.getAttribute('data-push-url')
            const params = new URLSearchParams(e.detail.parameters).toString()
            const url = `${window.location.origin}${path}?${params}`
            window.history.pushState({}, '', url);
        })
    },
    /*
    onEvent : function(name, e) {
        console.log("onEvent was run with name=" + name) // this never runs
        if (name === "htmx:configRequest") {
            console.log("push-url-w-params was run")
            const path = e.target.getAttribute('data-push-url')
            const params = new URLSearchParams(e.detail.parameters).toString()
            const url = `${window.location.origin}${path}?${params}`
            window.history.pushState({}, '', url);
        }
    }
        */
})
//console.log(htmx.extensions["push-url-w-params"])