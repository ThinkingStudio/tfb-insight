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
                label: 'Top Java Fullstack frameworks',
                language: 'Java',
                classification: 'Fullstack'
            },
            {
                label: 'Top PHP frameworks',
                language: 'PHP'
            },
            {
                label: 'Top Python frameworks',
                language: 'Python'
            },
            {
                label: 'Top Ruby frameworks',
                language: 'Ruby'
            },
            {
                label: 'Top Scala frameworks',
                language: 'Scala'
            },
            {
                label: 'Top Go frameworks',
                language: 'Go'
            },
            {
                label: 'Top JVM frameworks',
                technology: 'JVM'
            },
            {
                label: 'Top .Net frameworks',
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