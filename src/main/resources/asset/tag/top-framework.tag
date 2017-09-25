<top-framework>
    <h2 if="{filter}">{filter.label}</h2>
    <div class="radio-panel">
        <div class="radio {current:currentTest === test.id}" each="{test in tests}" onclick="{selectTest}">
            {test.label}
        </div>
    </div>
    <div id="canvas-container">
        <canvas id="chart"></canvas>
    </div>
    <style>
        .radio-panel > .radio {
            display: inline-block;
            margin: 8px;
            padding: 5px 15px;
            background: #444;
            border-radius: 5px;
            cursor: pointer;
        }
        .radio-panel > .radio:hover, .radio-panel > .radio.current {
            background: #aaa;
            color: #000;
        }
    </style>
    <script>
        Chart.defaults.global.defaultFontColor = '#fff';
        var self = this
        var nav = self.parent.parent.tags.nav.tags['nav-top-n']
        self.on('mount', function() {
            self.fetchData($.extend({}, nav.currentFilter, {test: self.currentTest}))
        })
        self.on('update', function() {
            self.fetchData($.extend({}, nav.currentFilter, {test: self.currentTest}))
        })
        self.tests = [
            {id: 'db', label: 'Single Query'},
            {id: 'query', label: 'Multiple Queries'},
            {id: 'update', label: 'Updates'},
            {id: 'fortune', label: 'Fortune'},
            {id: 'json', label: 'JSON'},
            {id: 'plaintext', label: 'Plain Text'},
            {id: 'density', label: 'Code Density'}
        ]
        self.currentTest = 'db'
        self.filter = {}
        riot.store.on('open', function(param) {
            console.log(param)
            if (param.view !== 'top-framework') {
                return
            }
            self.filter = param.filter
            self.update()
            var payload = $.extend(true, self.filter, {test: self.currentTest.id})
            self.fetchData(payload)
        })

        selectTest(e) {
            self.currentTest = e.item.test.id
            self.update()
        }

        fetchData(payload) {
            var endpoint = '/api/v1/chart/framework'
            if ('language' === payload.target) {
                endpoint = '/api/v1/chart/language'
            }
            $.getJSON(endpoint, payload, function (data) {
                var config = {
                    type: 'horizontalBar',
                    data: data,
                    options: {
                        legend: {
                            display: data.datasets.length > 1,
                        },
                        scales: {
                            xAxes: [{
                                ticks: {
                                    beginAtZero: true
                                },
                                scaleLabel: {
                                    fontColor: '#aaa'
                                }
                            }]
                        }
                    }
                };
                if (self.chart) {
                    self.chart.destroy()
                }
                var ctx = document.getElementById("chart");
                self.chart = new Chart(ctx, config)
            })
        }
    </script>
</top-framework>