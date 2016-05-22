
var LBSTracePlugin = function () {
};

LBSTracePlugin.prototype.call_native = function (name, args, callback, error_callback) {
  var ret = cordova.exec(callback, error_callback, 'LBSTracePlugin', name, args);
  return ret;
}

LBSTracePlugin.prototype.configure = function (serviceId, entityName, traceType, callback, error_callback) {
  this.call_native("configure", [serviceId, entityName, traceType], callback, error_callback);
}

LBSTracePlugin.prototype.setInterval = function (gatherInterval, packInterval, callback, error_callback) {
  this.call_native("setInterval", [gatherInterval, packInterval], callback, error_callback);
}

LBSTracePlugin.prototype.startTrace = function (callback, error_callback) {
  this.call_native("startTrace", [], callback, error_callback);
}

LBSTracePlugin.prototype.stopTrace = function (callback, error_callback) {
  this.call_native("stopTrace", [], callback, error_callback);
}

LBSTracePlugin.prototype.queryRealtimeLoc = function (callback, error_callback) {
  this.call_native("queryRealtimeLoc", [], callback, error_callback);
}

LBSTracePlugin.prototype.queryEntityList = function (entityNames, columnKey, activeTime, callback, error_callback) {
  this.call_native("queryEntityList", [entityNames, columnKey, activeTime], callback, error_callback);
}

LBSTracePlugin.prototype.queryHistoryTrack = function (startTime, endTime, callback, error_callback) {
  this.call_native("queryHistoryTrack", [startTime, endTime], callback, error_callback);
}

module.exports = new LBSTracePlugin();

