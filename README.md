# PopularMovies
Add key in build.gradle (Module: app) 

buildTypes.each {
        it.buildConfigField 'String', 'TMDb_API_KEY','"YOUR KEY HERE"'
    }
