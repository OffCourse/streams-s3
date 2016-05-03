try {
    require("source-map-support").install();
} catch(err) {
}
require("./target/main.out/goog/bootstrap/nodejs.js");
require("./target/main.out/cljs/core.js");
require("./target/main.out/cljs/core/async/impl/protocols.js");
require("./target/main.out/cljs/core/async/impl/buffers.js");
require("./target/main.out/cljs/core/async/impl/ioc_helpers.js");
require("./target/main.out/cljs/core/async/impl/dispatch.js");
require("./target/main.out/cljs/core/async/impl/channels.js");
require("./target/main.out/cljs/core/async/impl/timers.js");
require("./target/main.out/cljs/core/async.js");
require("./target/main.out/cljs/nodejs.js");
require("./target/main.out/app/core.js");
goog.require("app.core");
exports.handler = app.core.handler;
