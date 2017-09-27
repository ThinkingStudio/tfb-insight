<main>
    <welcome if="{view === 'welcome'}"></welcome>
    <top-n if="{view === 'top-n'}"></top-n>
    <framework if="{view === 'framework'}"></framework>
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
            riot.store.trigger(self.lastEvent.id, self.lastEvent.param)
            self.lastEvent = false
        })
    </script>
</main>