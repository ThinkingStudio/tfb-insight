<nav-framework>
    <div class="heading"><i class="fa fa-institution"></i>&nbsp;Frameworks</div>
    <ul class="language-list menu">
        <li each="{frameworkList, language in frameworks}" class="{language} {current: currentLanguage === language}" >
            <div class="language" onclick="{toggleLanguage}">
                <i class="fa fa-folder" if="{currentLanguage !== language}"></i>
                <i class="fa fa-folder-open" if="{currentLanguage === language}"></i>
                {language}
                <span class="float-right badge hover-visible">{frameworkList.length}</span>
            </div>
            <ul class="framework-list menu" if="{currentLanguage === language}">
                <li each="{framework in frameworkList}" onclick="{viewFramework}" class="{current: currentFramework === framework}">
                    <i class="fa fa-file-text"></i>
                    {framework}
                </li>
            </ul>
        </li>
    </ul>
    <script>
        var self = this
        self.frameworks = {}
        self.currentLanguage = false
        self.currentFramework = false
        self.on('mount', function() {
            self.fetchFrameworks()
        })
        fetchFrameworks() {
            $.getJSON('/api/v1/framework', function(data) {
                self.frameworks = data
                self.update()
            })
        }
        toggleLanguage(e) {
            if (e.item.language !== self.currentLanguage) {
                self.currentLanguage = e.item.language
            } else {
                self.currentLanguage = false
            }
        }
        viewFramework(e) {
            self.currentFramework = e.item.framework
            riot.store.trigger('open', {view: 'framework', framework: self.currentFramework, language: self.currentLanguage});
        }
    </script>
</nav-framework>