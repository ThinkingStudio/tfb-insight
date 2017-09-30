<nav>
    <nav-top-n></nav-top-n>
    <br/>
    <nav-framework></nav-framework>
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
        showAbout() {
            riot.store.trigger('open', {view: 'welcome'})
        }
    </script>
</nav>