<framework>
    <h2>{framework}</h2>
    <div class="framework-performance">
        <h4>Single Query</h4>
        <canvas id="single-query"></canvas>
    </div>
    <script>
        var self = this
        self.framework = false
        self.on('mount', function() {
            riot.store.trigger('resend-last-event');
        })
        riot.store.on('open', function(param) {
            self.framework = param.framework
            self.update()
        })
    </script>
</framework>