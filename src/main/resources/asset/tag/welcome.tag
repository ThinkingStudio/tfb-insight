<welcome>
    <p>
        TFB Insight is an application that analyse <a href="https://www.techempower.com/benchmarks/previews/round15/">TechEmpower Framework Benchmark</a> data and provides more insight into it.
    </p>
    <p>
        Please use left navigation bar to explore
    </p>
    <div class="faq">
        <h4>FAQ</h4>
        <section>
            <div class="q">Where is the data come from?</div>
            <div class="a">
                <p>There are two places for TFB insight to grab the data source:</p>
                <ul>
                    <li>
                        The test results data come from <a href="https://www.techempower.com/benchmarks/previews/round15/results/round15/ph.json">
                            https://www.techempower.com/benchmarks/previews/round15/results/round15/ph.json
                        </a>
                    </li>
                    <li>
                        The source code is cloned from <a href="https://github.com/TechEmpower/FrameworkBenchmarks/">
                            https://github.com/TechEmpower/FrameworkBenchmarks/
                        </a>
                    </li>
                </ul>
            </div>
        </section>
        <section>
            <div class="q">What is code density and how is it calculated?</div>
            <div class="a">
                <p>Code density is a measurement to help see how expressive it is to code application using the frameework/platform. </p>
                <p>
                    Code density is calculated using the following approache:
                </p>
                <pre>code_density=Sigma(num-of-test-for-type * weight-of-type) / loc * 100</pre>
                <p>
                    Where the weight of test type is arbitrarily determined as:
                </p>
                <ul>
                    <li>plaintext - 1</li>
                    <li>json - 1.2</li>
                    <li>db - 2.0</li>
                    <li>query - 2.2</li>
                    <li>update - 2.5</li>
                    <li>fortune - 2.2</li>
                </ul>
            </div>
        </section>
    </div>
    <p>
        TFB Insight is powered by <a href="https://github.com/actframework/actframework">ActFramework</a>, <a href="http://riotjs.com/">RiotJs</a> and <a href="http://www.chartjs.org">ChartJs</a>
    </p>
    <style>
        section {
            margin-top: 20px;
            position: relative;
            display: block
        }
        section .q {
            margin-bottom: 10px;
            font-weight: 600;
        }
        section ul {
            list-style: disc;
            padding-left: 20px;
        }
    </style>
</welcome>