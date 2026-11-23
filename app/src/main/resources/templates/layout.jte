
@import gg.jte.Content

@param String pageTitle
@param String flash = null
@param Content content

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle}</title>
    <link href="/css/main.css" rel="stylesheet" />
</head>
<body>
    <nav class="mx-auto max-w-3xl px-4 py-4">
        <a href="/" class="text-xl font-medium">Анализатор страниц</a>
    </nav>

    <main class="mx-auto max-w-3xl px-4 py-4">
        @if (flash != null)
            <div role="alert" class="mb-4 rounded bg-blue-100 p-4">
                ${flash}
            </div>
        @endif

        ${content}
    </main>
</body>
</html>
