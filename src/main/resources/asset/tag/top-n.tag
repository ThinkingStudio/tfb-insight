<top-n>
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
        function numberWithCommas(x) {
            return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        }
        Chart.defaults.global.defaultFontColor = '#fff';
        Chart.plugins.register({
            afterDatasetsDraw: function(chart, easing) {
                // To only draw at the end of animation, check for easing === 1
                var ctx = chart.ctx;
                chart.data.datasets.forEach(function (dataset, i) {
                    var meta = chart.getDatasetMeta(i);
                    if (!meta.hidden) {
                        meta.data.forEach(function(element, index) {
                            // Draw the text in black, with the specified font
                            var fontSize = 12;
                            var fontStyle = 'bold';
                            var fontFamily = '"Roboto Mono", Consolas';
                            ctx.font = Chart.helpers.fontString(fontSize, fontStyle, fontFamily);
                            // Just naively convert to string for now
                            var dataString = dataset.data[index].toString();
                            if (self.currentTest !== 'density') {
                                dataString = numberWithCommas(dataString) + '/s';
                            } else {
                                dataString = parseFloat(Math.round(dataset.data[index] * 100) / 100).toFixed(2) + "%";
                            }
                            // Make sure alignment settings are correct
                            ctx.textAlign = 'center';
                            ctx.textBaseline = 'middle';
                            var x = element._model.x - (dataString.length * 5)
                            if (x < 220) {
                                x = 220;
                            }
                            var y = element._model.y
                            var backup = {
                                fillStyle: ctx.fillStyle,
                                shadowColor: ctx.shadowColor,
                                shadowOffsetX: ctx.shadowOffsetX,
                                shadowOffsetY : ctx.shadowOffsetY,
                                shadowBlur: ctx.shadowBlur
                            }
                            ctx.fillStyle = 'rgb(255,255,255)';
                            ctx.shadowColor = 'black';
                            ctx.shadowBlur = 2;
                            ctx.shadowOffsetX = 1;
                            ctx.shadowOffsetY = 1;
                            ctx.fillText(dataString, x, y);
                            ctx.fillStyle = backup.fillStyle
                            ctx.shadowColor = backup.shadowColor
                            ctx.shadowOffsetX = backup.shadowOffsetX
                            ctx.shadowOffsetY = backup.shadowOffsetY
                            ctx.shadowBlur = backup.shadowBlur
                        });
                    }
                });
            }
        });
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
            if (param.view !== 'top-n') {
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
</top-n>