<main>
    <welcome if="{view === 'welcome'}"></welcome>
    <top-n if="{view === 'top-n'}"></top-n>
    <script>
        var self = this
        self.view = 'welcome'
        riot.store.on('open', function(param) {
            self.view = param.view
            self.update()
        })
    </script>
</main>