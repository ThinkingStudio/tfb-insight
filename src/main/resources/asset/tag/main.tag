<main>
    <welcome class="{invisible: view !== 'welcome'}"></welcome>
    <top-n class="{invisible: view !== 'top-n'}"></top-n>
    <framework class="{invisible: view !== 'framework'}"></framework>
    <style>
        .invisible {
            display: none
        }
    </style>
    <script>
        var self = this
        self.view = 'welcome'
        self.lastEvent = false
        riot.store.on('open', function(param) {
            self.view = param.view
            self.lastEvent = {
                id: 'open',
                param: param
            }
            self.update()
        })
        riot.store.on('resend-last-event', function() {
            if (!self.lastEvent) return
            var lastEvent = self.lastEvent
            self.lastEvent = false
            riot.store.trigger(lastEvent.id, lastEvent.param)
        })
    </script>
</main>