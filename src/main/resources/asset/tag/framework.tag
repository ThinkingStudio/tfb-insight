<framework>
    <div if="{noResult}">
        There is no test results for this framework
    </div>
    <div if="{!noResult}">
        <div class="brief">
            <p>
                <b>{_.capitalize(framework)}</b> is a <b>{classification}</b>
                <virtual if="{'Platform' !== classification}">framework</virtual> of <b>{language}</b>.
            </p>
            <p>
                <i class="fa fa-github"></i>
                <a target="_blank" href="https://github.com/TechEmpower/FrameworkBenchmarks/tree/master/frameworks{srcPath}">https://github.com/TechEmpower/FrameworkBenchmarks/tree/master/frameworks{srcPath}</a>
            </p>
        </div>
        <div class="section code-density" if="{densityInfo && densityInfo.framework}">
            <h3><i class="fa fa-binoculars"></i> Code density analysis</h3>
            <br>
            <table>
                <thead>
                <tr>
                    <th>{framework}</th>
                    <th>{language} median</th>
                    <th>{language} top</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>{densityInfo.framework.toFixed(2)}%</td>
                    <td>{densityInfo.langMedian.toFixed(2)}%</td>
                    <td>{densityInfo.langTop.toFixed(2)}%</td>
                </td>
                </tbody>
            </table>
            <p>
                <b>{_.capitalize(framework)}</b>
                <virtual if="{densityInfo.framework > 7}">
                    is an expressive framework/platform. It generally takes less lines of code
                    to develop application using <b>{_.capitalize(framework)}</b>
                </virtual>
                <virtual if="{densityInfo.framework <= 7 && densityInfo.framework > 4}">
                    is moderated framework/platform in terms of expressiveness.
                </virtual>
                <virtual if="{densityInfo.framework <= 4}">
                    is relatively less expressive framework/platform. It might takes more lines
                    of code to develop application using <b>{_.capitalize(framework)}</b>
                </virtual>
            </p>
        </div>
        <div class="section tests">
            <h3><i class="fa fa-line-chart"></i> Test Results</h3>
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
    </div>
    <style>
        .framework-performance {
            width: 640px;
            display: inline-block;
            margin: 20px;
        }
        h3 {
            margin-top: 40px;
            margin-bottom: 5px;
            font-weight: 700;
            display: block;
            border-bottom: 1px solid #555;
        }
        .tests h3{
            margin-bottom: -5px;
        }
        table, th, td, thead, tbody {
            border: 1px solid #888;
            border-collapse: collapse;
            padding: 2px 8px;
        }
        th {
            text-align: center;
            background: #aaa;
            color: #222
        }
        td {
            text-align: right;
        }
    </style>
    <script>
        var self = this
        self.framework = false
        self.language = false
        self.noResult = false
        self.densityDesc = false
        self.densityInfo = {}
        self.charts = {}
        self.histogram = {}
        self.classification = false
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
        getDensityDescriptor() {
            var di = self.densityInfo
            if (!di) {
                return 'Missing code density info for this framework';
            }
            var desc = self.framework + "'s code density level is: "
            if (di.framework === di.langTop) {
                desc = self.framework + ' is very expressive framework with code density of '
            } else if (di.framework > di.langMedian) {
                var desc = self.framework + " is an expressive framework with code density of "
            }
            return desc
        }
        fetchData() {
            self.destroyCharts()
            $.getJSON('/api/v1/chart/framework/' + self.framework, function (retVal) {
                if (retVal.noResult) {
                    self.noResult = true
                    self.update()
                    return
                }
                self.noResult = false
                self.srcPath = retVal.srcPath
                self.classification = retVal.classification
                self.densityInfo = retVal.densityInfo
                self.densityDesc = self.getDensityDescriptor()
                var dataset = retVal.testResults
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
                                        beginAtZero: false
                                    },
                                    scaleLabel: {
                                        fontColor: '#aaa'
                                    }
                                }]
                            }
                        }
                    };
                    var ctx = document.getElementById("chart-" + db);
                    if (!ctx) {
                        console.log('cannot find html element by chart-' + db)
                        return
                    }
                    if (ctx.chart) {
                        ctx.chart.destroy()
                    }
                    var chart = new Chart(ctx, config)
                    ctx.chart = chart
                })
            })
        }
    </script>
</framework>