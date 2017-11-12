<nav-vs>
    <div class="heading pointer" onclick="{toggleDisplay}">
        <i class="fa fa-group"></i>
        &nbsp;
        ActFramework VS ?
        <i class="fa fa-angle-right" if="{collapsed}"></i>
        <i class="fa fa-angle-up" if="{!collapsed}"></i>
    </div>
    <ul class="vs-list menu nav-vs" if="{!collapsed}">
        <li each="{filter in filters}"  onclick="{selectFilter}" class="{current: currentFilter === filter}">
            <i class="fa fa-handshake-o"></i>
            {filter.label}
        </li>
    </ul>
    <script>
        var self = this
        self.filters = [
            {
                label: 'ActFramework VS Spring',
                key: 'actframework:spring'
            },
            {
                label: 'ActFramework VS Dropwizard',
                key: 'actframework:dropwizard'
            },
            {
                label: 'ActFramework VS Play2',
                key: 'actframework:play2'
            },
            {
                label: 'ActFramework VS Play1',
                key: 'actframework:play1'
            },
            {
                label: 'ActFramework VS Vertx',
                key: 'actframework:vertx-web'
            },
            {
                label: 'ActFramework VS Spark',
                key: 'actframework:spark'
            },
            {
                label: 'ActFramework VS Ninja',
                key: 'actframework:ninja-standalone'
            },
            {
                label: 'ActFramework VS jooby',
                key: 'actframework:jooby'
            },
            {
                label: 'ActFramework VS tapestry',
                key: 'actframework:tapestry'
            },
            {
                label: 'ActFramework VS wicket',
                key: 'actframework:wicket'
            },
            {
                label: 'ActFramework VS WildFly-EE7',
                key: 'actframework:wildfly-ee7'
            },
        ]
        self.currentFilter = false
        self.collapsed = true

        var r = route.create()
        r("vs/*", function(key) {
            gtag('event', 'view_vs', {
                event_label: key,
                items: key.split(':')
            })
            self._selectFilter(key)
        });

        toggleDisplay() {
            self.collapsed = !self.collapsed
        }

        selectFilter(e) {
            route('vs/' + e.item.filter.key)
        }

        _selectFilter(key) {
            for (var i = 0, j = self.filters.length; i < j; ++i) {
                var filter = self.filters[i]
                if (filter.key === key) {
                    self.currentFilter = filter
                    riot.store.trigger('open', {view: 'top-n', filter: $.extend({vsKey: self.currentFilter.key}, self.currentFilter)});
                    self.update()
                    return
                }
            }
            var filter = {
                label: key.replace(/:/g, ' vs '),
                key: key
            }
            self.currentFilter = filter
            riot.store.trigger('open', {view: 'top-n', filter: $.extend({vsKey: self.currentFilter.key}, self.currentFilter)});
        }
    </script>
</nav-vs>