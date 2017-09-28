<top-n>
    <div class="radio-panel">
        <div class="radio {current:currentTest === test.id}" each="{test in tests}" onclick="{selectTest}">
            {test.label}
        </div>
    </div>
    <div if="{hideDesc}" class="brief" onclick="{showDescription}" style="cursor:pointer;">
        <i class="fa fa-ellipsis-h" style="float: right;margin-top:4px"></i>
        Description
    </div>
    <div if="{'db' === currentTest && !hideDesc}" class="brief">
        <div class="btn-right-corner" onclick="{hideDescription}">
            <i class="fa fa-angle-up"></i>
        </div>
        <p>
            In Single Query Test each request is processed by fetching a single row from a simple database table. That row is then serialized as a JSON response.
        </p>
        <p>
            The test is running at 8, 16, 32, 64, 128 and 256 concurrency levels, and the data presented below is the best result.
        </p>
    </div>
    <div if="{'query' === currentTest && !hideDesc}" class="brief">
        <div class="btn-right-corner" onclick="{hideDescription}">
            <i class="fa fa-angle-up"></i>
        </div>
        <p>
            In Multiple Queries Test, each request is processed by fetching multiple rows from a simple database table and serializing these rows as a JSON response. The test is run multiple times: testing 1, 5, 10, 15, and 20 queries per request. All tests are run at 256 concurrency
        </p>
        <p>
            The data presented below is the result of 20 queries per request case
        </p>
    </div>
    <div if="{'update' === currentTest && !hideDesc}" class="brief">
        <div class="btn-right-corner" onclick="{hideDescription}">
            <i class="fa fa-angle-up"></i>
        </div>
        <p>
            In this test, each request is processed by fetching multiple rows from a simple database table, converting the rows to in-memory objects, modifying one attribute of each object in memory, updating each associated row in the database individually, and then serializing the list of objects as a JSON response. The test is run multiple times: testing 1, 5, 10, 15, and 20 updates per request. Note that the number of statements per request is twice the number of updates since each update is paired with one query to fetch the object. All tests are run at 256 concurrency.
        </p>
        <p>
            The data presented below is the result of 20 queries per request case
        </p>
    </div>
    <div if="{'json' === currentTest && !hideDesc}" class="brief">
        <div class="btn-right-corner" onclick="{hideDescription}">
            <i class="fa fa-angle-up"></i>
        </div>
        <p>
            In this test, each response is a JSON serialization of a freshly-instantiated object that maps the key message to the value Hello, World!
        </p>
        <p>
            The test is running at 8, 16, 32, 64, 128 and 256 concurrency levels, and the data presented below is the best result.
        </p>
    </div>
    <div if="{'plaintext' === currentTest && !hideDesc}" class="brief">
        <div class="btn-right-corner" onclick="{hideDescription}">
            <i class="fa fa-angle-up"></i>
        </div>
        <p>
            In this test, the framework responds with the simplest of responses: a "Hello, World" message rendered as plain text. The size of the response is kept small so that gigabit Ethernet is not the limiting factor for all implementations. HTTP pipelining is enabled and higher client-side concurrency levels are used for this test
        </p>
        <p>
            The test is running at 256, 1024, 4096, 16,384 concurrency levels, and the data presented below is the best result.
        </p>
    </div>
    <div if="{'fortune' === currentTest && !hideDesc}" class="brief">
        <div class="btn-right-corner" onclick="{hideDescription}">
            <i class="fa fa-angle-up"></i>
        </div>
        <p>
            In this test, the framework's ORM is used to fetch all rows from a database table containing an unknown number of Unix fortune cookie messages (the table has 12 rows, but the code cannot have foreknowledge of the table's size). An additional fortune cookie message is inserted into the list at runtime and then the list is sorted by the message text. Finally, the list is delivered to the client using a server-side HTML template. The message text must be considered untrusted and properly escaped and the UTF-8 fortune messages must be rendered properly.
        </p>
        <p>
            The test is running at 8, 16, 32, 64, 128 and 256 concurrency levels, and the data presented below is the best result.
        </p>
    </div>
    <div if="{'density' === currentTest && !hideDesc}" class="brief">
        <div class="btn-right-corner" onclick="{hideDescription}">
            <i class="fa fa-angle-up"></i>
        </div>
        <p>
            This is an indication about how efficiency the testing project code is by measuring the LoC and the tests.
            At the moment we are using the following approach to determine test weight in the calculating:
        </p>
        <table class="density">
            <tbody>
            <tr>
                <td>Plain Test</td>
                <td>1</td>
            </tr>
            <tr>
                <td>JSON</td>
                <td>1.2</td>
            </tr>
            <tr>
                <td>Single Query</td>
                <td>2</td>
            </tr>
            <tr>
                <td>Multiple Queries</td>
                <td>2.2</td>
            </tr>
            <tr>
                <td>Data Updates</td>
                <td>2.5</td>
            </tr>
            <tr>
                <td>Fortune</td>
                <td>2.2</td>
            </tr>
            </tbody>
        </table>
        <p>Note this measurement does not take the Fortune template into consideration, neither does it calculate the configuration files</p>
    </div>
    <div id="canvas-container">
        <canvas id="chart"></canvas>
    </div>
    <style>
        div.btn-right-corner {
            position: absolute;
            right: 10px;
            top: 0px;
            cursor: pointer;
        }
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
        .brief {
            font-size: .9em;
            color: white;
            margin: 6px;
            padding: 2px 15px;
            background: #444;
            position: relative;
            width: 888px;
        }
        table.density td {
            text-align: right;
            padding-left: 10px;
        }
    </style>
    <script>
        var self = this
        self.mounted = false
        self.hideDesc = true
        self.currentTest = 'db'
        self.filter = false
        self.on('mount', function() {
            riot.store.trigger('resend-last-event');
            var chartHolder = document.getElementById("chart")
            chartHolder.onclick = function(e) {
                var chart = chartHolder.chart;
                if (chart) {
                    var element = chart.getElementAtEvent(e)
                    if (!element) return
                    element = element[0]
                    if (!element) return
                    var label = element._model.label
                    console.log(label)
                    // get framework from '[language] framework | database'
                    var id = label.indexOf(']')
                    if (id < 0) {
                        // it must be language bar
                        riot.store.trigger('open', {view: 'top-n', filter: {language: label, label: label + ' frameworks'}})
                        return
                    }
                    var id2 = label.indexOf('|')
                    var language = label.substring(1, id)
                    var framework = label.substring(id + 2, id2 - 1)
                    riot.store.trigger('open', {view: 'framework', framework: framework, language: language})
                }
            }
        })

        riot.store.on('open', function(param) {
            if (param.view === 'top-n') {
                self.filter = param.filter
                self.fetchData($.extend({}, self.filter, {test: self.currentTest}))
                riot.store.trigger('set-heading', {heading: self.filter.label + ' - ' + self.currentTest});
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
        selectTest(e) {
            self.currentTest = e.item.test.id
            self.update()
            riot.store.trigger('set-heading', {heading: self.filter.label + ' - ' + self.currentTest});
            self.fetchData($.extend({}, self.filter, {test: self.currentTest}))
        }
        hideDescription() {
            self.hideDesc = true
        }
        showDescription() {
            self.hideDesc = false
        }
        fetchData(payload) {
            if (self.chart) {
                self.chart.destroy()
            }
            var endpoint = '/api/v1/chart/framework'
            var type = 'horizontalBar'
            if ('language' === payload.target) {
                endpoint = '/api/v1/chart/language'
                type = 'bar'
            }
            $.getJSON(endpoint, payload, function (data) {
                var config = {
                    type: type,
                    target: payload.target,
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
                var ctx = document.getElementById("chart");
                if (ctx.chart) {
                    ctx.chart.destroy()
                }
                self.chart = new Chart(ctx, config)
                ctx.chart = self.chart
            })
        }
    </script>
</top-n>