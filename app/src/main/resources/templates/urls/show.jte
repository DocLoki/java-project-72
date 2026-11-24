
@import hexlet.code.models.Url
@import hexlet.code.models.UrlCheck
@import java.util.List

@param Url url
@param List<UrlCheck> checks
@param String flash = null

@template.layout(
    pageTitle = "Информация о сайте",
    flash = flash,
    content = @`
        <div class="mx-auto max-w-3xl">
            <h1 class="mb-6 text-3xl font-light">Информация о сайте</h1>

            <table data-test="url" class="w-full text-left">
                <tbody>
                    <tr class="border-b border-gray-200">
                        <td class="p-3 font-medium">ID</td>
                        <td class="p-3">${url.getId()}</td>
                    </tr>
                    <tr class="border-b border-gray-200">
                        <td class="p-3 font-medium">Имя</td>
                        <td class="p-3">${url.getName()}</td>
                    </tr>
                    <tr class="border-b border-gray-200">
                        <td class="p-3 font-medium">Дата создания</td>
                        <td class="p-3">${url.getCreatedAt().toLocalDateTime().toLocalDate().toString()}</td>
                    </tr>
                </tbody>
            </table>

            <form method="post" action="/urls/${url.getId()}/checks" class="mt-6">
                <input
                    type="submit"
                    value="Запустить проверку"
                    class="cursor-pointer rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
                >
            </form>

            <h2 class="mb-4 mt-8 text-2xl font-light">Проверки</h2>

            <div class="overflow-x-auto">
                <table data-test="checks" class="w-full text-left">
                    <thead>
                        <tr class="border-b border-gray-200">
                            <th class="p-3">ID</th>
                            <th class="p-3">Код ответа</th>
                            <th class="p-3">h1</th>
                            <th class="p-3">title</th>
                            <th class="p-3">description</th>
                            <th class="p-3">Дата создания</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(var check : checks)
                            <tr class="border-b border-gray-200">
                                <td class="p-3">${check.getId()}</td>
                                <td class="p-3">${check.getStatusCode()}</td>
                                <td class="p-3">${check.getH1() == null ? "" : check.getH1().length() > 200 ? check.getH1().substring(0, 200) + "..." : check.getH1()}</td>
                                <td class="p-3">${check.getTitle() == null ? "" : check.getTitle().length() > 200 ? check.getTitle().substring(0, 200) + "..." : check.getTitle()}</td>
                                <td class="p-3">${check.getDescription() == null ? "" : check.getDescription().length() > 200 ? check.getDescription().substring(0, 200) + "..." : check.getDescription()}</td>
                                <td class="p-3">${check.getCreatedAt().toLocalDateTime().toLocalDate().toString()}</td>
                            </tr>
                        @endfor
                    </tbody>
                </table>
            </div>

            <a href="/urls" class="mt-6 inline-block text-blue-600 hover:underline">
                К списку сайтов
            </a>
        </div>
    `
)
