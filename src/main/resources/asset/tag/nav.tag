<nav>
    <nav-top-n></nav-top-n>
    <br/>
    <nav-framework></nav-framework>
    <br/>
    <nav-vs></nav-vs>
    <br/>
    <div class="heading pointer about" onclick="{showAbout}">
        <i class="fa fa-info"></i>
        &nbsp;
        About
    </div>
    <style>
        nav {
            font-size: 0.85em;
        }
        nav .heading {
            font-size: 1em;
        }
        nav .about.heading {
            cursor: pointer;
        }
    </style>
    <script>
        var r = route.create()
        r('about', function() {
            riot.store.trigger('open', {view: 'welcome'})
        })
        showAbout() {
            route('about')
        }
        this.on('mount', route.exec)
    </script>
</nav>