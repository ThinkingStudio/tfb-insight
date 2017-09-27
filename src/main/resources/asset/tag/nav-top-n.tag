<nav-top-n>
    <div class="heading">Top N</div>
    <ul class="top-n-list menu">
        <li each="{filter in filters}"  onclick="{selectFilter}" class="{current: currentFilter === filter}">
            <i class="fa fa-bar-chart"></i>
            {filter.label}
        </li>
    </ul>
    <script>
        var self = this
        self.filters = [
            {
                label: 'Java Fullstack/Micro frameworks',
                language: 'Java',
                classification: '(Fullstack|Micro)'
            },
            {
                label: 'PHP frameworks',
                language: 'PHP'
            },
            {
                label: 'Python frameworks',
                language: 'Python'
            },
            {
                label: 'Ruby frameworks',
                language: 'Ruby'
            },
            {
                label: 'Scala frameworks',
                language: 'Scala'
            },
            {
                label: 'Go frameworks',
                language: 'Go'
            },
            {
                label: 'JVM frameworks',
                technology: 'JVM'
            },
            {
                label: '.Net frameworks',
                technology: 'DOT_NET'
            },
            {
                label: 'Language Insight',
                target: 'language'
            }
        ]
        self.currentFilter = false

        selectFilter(e) {
            self.currentFilter = e.item.filter
            riot.store.trigger('open', {view: 'top-n', filter: self.currentFilter});
        }
    </script>
</nav-top-n>