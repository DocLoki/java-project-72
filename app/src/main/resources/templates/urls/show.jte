
@import hexlet.code.models.Url

@param Url url
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

            <a href="/urls" class="mt-6 inline-block text-blue-600 hover:underline">
                К списку сайтов
            </a>
        </div>
    `
)
