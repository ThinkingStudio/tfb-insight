<framework>
    <div>
        <div class="framework-performance" if="{hasData('db')}">
            <h4>Single Query</h4>
            <canvas id="chart-db"></canvas>
        </div>
        <div class="framework-performance" if="{hasData('query')}">
            <h4>Multiple Query</h4>
            <canvas id="chart-query"></canvas>
        </div>
        <div class="framework-performance" if="{hasData('update')}">
            <h4>Update</h4>
            <canvas id="chart-update"></canvas>
        </div>
        <div class="framework-performance" if="{hasData('fortune')}">
            <h4>Fortune</h4>
            <canvas id="chart-fortune"></canvas>
        </div>
        <div class="framework-performance" if="{hasData('json')}">
            <h4>JSON</h4>
            <canvas id="chart-json"></canvas>
        </div>
        <div class="framework-performance" if="{hasData('plaintext')}">
            <h4>Plain Text</h4>
            <canvas id="chart-plaintext"></canvas>
        </div>
    </div>
    <style>
        .framework-performance {
            width: 640px;
            display: inline-block;
            margin: 20px;
        }
    </style>
    <script>
        var self = this
        self.framework = false
        self.language = false
        self.charts = {}
        self.histogram = {}
        self.on('mount', function () {
            riot.store.trigger('resend-last-event');
        })
        riot.store.on('open', function (param) {
            if (param.view !== 'framework') {
                return
            }
            self.framework = param.framework
            self.language = param.language
            self.fetchData()
            riot.store.trigger('set-heading', {heading: self.framework + '/' + self.language});
        })
        hasData(test) {
            var _hasData = typeof(self.histogram[test]) !== 'undefined'
            return _hasData
        }
        destroyCharts() {
            $.each(self.charts, function(db, chart) {
                if (chart) chart.destroy()
            })
        }
        fetchData() {
            $.getJSON('/api/v1/chart/framework/' + self.framework, function (dataset) {
                self.histogram = dataset
                self.update()
                self.destroyCharts()
                $.each(dataset, function (db, data) {
                    if (!data && !data.dataset) {
                        return
                    }
                    if (self.charts[db]) {
                        self.charts[db].destroy()
                    }
                    var config = {
                        type: 'line',
                        data: data,
                        options: {
                            legend: {
                                display: data.datasets.length > 1,
                            },
                            scales: {
                                yAxes: [{
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
                    var ctx = document.getElementById("chart-" + db);
                    self.charts[db] = new Chart(ctx, config)
                })
            })
        }
    </script>
</framework>