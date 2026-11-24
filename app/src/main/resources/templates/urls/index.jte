
@import java.util.List
@import hexlet.code.models.Url

@param List<Url> urls

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
                            <th class="p-3">Дата последней проверки</th>
                            <th class="p-3">Код ответа</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for (Url url : urls)
                            <tr class="border-t border-gray-200">
                                <td class="p-3">${url.getId()}</td>
                                <td class="p-3">
                                    <a
                                        href="/urls/${url.getId()}"
                                        class="text-blue-600 hover:underline"
                                    >${url.getName()}</a>
                                </td>
                                <td class="p-3">${url.getCreatedAt().toLocalDateTime().toLocalDate().toString()}</td>
                                <td class="p-3">
                                    @if (url.getLastCheckCreatedAt() != null)
                                        ${url.getLastCheckCreatedAt().toLocalDateTime().toLocalDate().toString()}
                                    @endif
                                </td>
                                <td class="p-3">
                                    @if (url.getLastCheckStatusCode() != null)
                                        ${url.getLastCheckStatusCode()}
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
