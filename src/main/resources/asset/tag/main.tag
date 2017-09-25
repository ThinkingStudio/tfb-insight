<main>
    <welcome if="{view === 'welcome'}"></welcome>
    <top-framework if="{view === 'top-framework'}"></top-framework>
    <script>
        var self = this
        self.view = 'welcome'
        riot.store.on('open', function(param) {
            self.view = param.view
            self.update()
        })
    </script>
</main>