<top-n>
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
        var self = this
        self.on('mount', function() {
            riot.store.trigger('resend-last-event');
        })
        self.on('update', function() {
            riot.store.trigger('set-heading', {heading: self.filter.label + ' - ' + self.currentTest});
            self.fetchData($.extend({}, self.filter, {test: self.currentTest}))
        })
        riot.store.on('open', function(param) {
            if (param.view === 'top-n') {
                self.filter = param.filter
                self.update()
            }
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
</top-n>