<nav-top-n>
    <div class="heading pointer" onclick="{toggleDisplay}">
        <i class="fa fa-sort-amount-desc"></i>
        &nbsp;
        Top N
        <i class="fa fa-angle-right" if="{collapsed}"></i>
        <i class="fa fa-angle-up" if="{!collapsed}"></i>
    </div>
    <ul class="top-n-list menu nav-top-n" if="{!collapsed}">
        <li each="{filter in filters}"  onclick="{selectFilter}" class="{current: currentFilter === filter}">
            <i class="fa fa-bar-chart"></i>
            {filter.label}
        </li>
    </ul>
    <script>
        var self = this
        self.collapsed = false
        self.filters = [
            {
                label: 'Cross Languages',
                key: 'cross-languages'
            },
            {
                label: 'Java Fullstack/Micro frameworks',
                language: 'Java',
                classification: '(Fullstack|Micro)',
                key: 'java-full_micro'
            },
            {
                label: 'Java frameworks',
                language: 'Java',
                key: 'java'
            },
            {
                label: 'PHP frameworks',
                language: 'PHP',
                key: 'php'
            },
            {
                label: 'Python frameworks',
                language: 'Python',
                key: 'python'
            },
            {
                label: 'Ruby frameworks',
                language: 'Ruby',
                key: 'ruby'
            },
            {
                label: 'Scala frameworks',
                language: 'Scala',
                key: 'scala',
            },
            {
                label: 'Kotlin frameworks',
                language: 'Kotlin',
                key: 'kotlin',
            },
            {
                label: 'Go frameworks',
                language: 'Go',
                key: 'go'
            },
            {
                label: 'JVM frameworks',
                technology: 'JVM',
                key: 'jvm'
            },
            {
                label: '.Net frameworks',
                technology: 'DOT_NET',
                key: 'dot_net'
            },
            {
                label: 'Language Insight',
                target: 'language',
                key: 'languages'
            }
        ]
        self.currentFilter = false

        toggleDisplay() {
            self.collapsed = !self.collapsed
        }

        var r = route.create()
        r("top/*", function(key) {
            gtag('event', 'view_top', {
                event_label: key,
                item: key
            })
            self._selectFilter(key)
        });

        selectFilter(e) {
            route('top/' + e.item.filter.key)
        }

        _selectFilter(key) {
            for (var i = 0, j = self.filters.length; i < j; ++i) {
                var filter = self.filters[i]
                if (filter.key === key) {
                    self.currentFilter = filter
                    riot.store.trigger('open', {view: 'top-n', filter: self.currentFilter})
                    self.update()
                    return
                }
            }
            filter = {
                label: key,
                language: key,
                key: key
            }
            self.currentFilter = filter
            riot.store.trigger('open', {view: 'top-n', filter: self.currentFilter})
        }
    </script>
</nav-top-n>