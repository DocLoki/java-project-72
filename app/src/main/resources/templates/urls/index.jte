@import java.util.List
@import hexlet.code.models.UrlWithLastCheck
@import java.time.format.DateTimeFormatter

@param List<UrlWithLastCheck> urls

@template.layout(
    pageTitle = "Сайты",
    content = @`
        <div class="mx-auto max-w-3xl">
            <h1 class="mb-6 text-3xl font-light">Сайты</h1>

            <a
                href="/"
                class="mb-6 inline-flex items-center rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm transition hover:bg-gray-100 hover:text-blue-600"
            >
                ← Назад
            </a>

            <div class="overflow-x-auto rounded-lg border border-gray-200">
                <table data-test="urls" class="w-full text-left">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="p-3">ID</th>
                            <th class="p-3">Имя</th>
                            <th class="p-3">Дата создания</th>
                            <th class="p-3">Код ответа</th>
                            <th class="p-3">Дата последней проверки</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for (UrlWithLastCheck item : urls)
                            <tr class="border-t border-gray-200">
                                <td class="p-3">${item.url().getId()}</td>
                                <td class="p-3">
                                    <a
                                        href="/urls/${item.url().getId()}"
                                        class="text-blue-600 hover:underline"
                                    >${item.url().getName()}</a>
                                </td>
                                <td class="p-3">${item.url().getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}</td>
                                <td class="p-3">
                                    @if (item.lastCheckStatusCode() != null)
                                        ${item.lastCheckStatusCode()}
                                    @endif
                                </td>
                                <td class="p-3">
                                    @if (item.lastCheckCreatedAt() != null)
                                        ${item.lastCheckCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}
                                    @endif
                                </td>
                            </tr>
                        @endfor
                    </tbody>
                </table>
            </div>
        </div>
    `
)
