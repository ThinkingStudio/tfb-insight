<version>
<div id="version">
    {version.app.artifactId}-{version.app.version} powered by
    <a href="https://github.com/actframework/actframework">
    act-{version.act.version}
    </a>
<div>
<style>
#version {
    position: fixed;
    font-size: 10pt;
    font-family: 'Courier New', monospaced;
    top: 69px;
    right: 20px;
    z-index: 999;
}
</style>
<script>
var self = this
self.version = {}

self.on('mount', function() {
    $.getJSON('/~/version', function(version) {
        self.version = version
        self.update()
    })
})

</script>
</version>