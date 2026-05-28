import { useDrafts } from '../hooks/useDrafts'
import { DraftCard } from '../components/DraftCard'

const CHANNELS = ['NEWSLETTER', 'LINKEDIN', 'TWITTER']

const CHANNEL_STYLES = {
  NEWSLETTER: 'border-purple-400 text-purple-700',
  LINKEDIN: 'border-blue-500 text-blue-700',
  TWITTER: 'border-sky-400 text-sky-600',
}

export function DraftListPage() {
  const { data, loading, error } = useDrafts('PENDING_REVIEW')

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-10 w-10 border-4 border-purple-500 border-t-transparent" />
      </div>
    )
  }

  if (error) {
    return (
      <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-red-700 text-sm">
        <strong>Error al cargar borradores:</strong> {error}
      </div>
    )
  }

  const isEmpty = !data || CHANNELS.every((ch) => !data[ch]?.length)

  if (isEmpty) {
    return (
      <p className="text-center text-gray-400 py-16">
        No hay borradores pendientes de revisión.
      </p>
    )
  }

  return (
    <div className="flex flex-col gap-8">
      {CHANNELS.map((channel) => {
        const drafts = data?.[channel] ?? []
        if (!drafts.length) return null

        const borderColor = CHANNEL_STYLES[channel] ?? 'border-gray-400 text-gray-700'

        return (
          <section key={channel}>
            <h2 className={`text-base font-semibold uppercase tracking-widest border-l-4 pl-3 mb-4 ${borderColor}`}>
              {channel}
              <span className="ml-2 text-xs font-normal normal-case tracking-normal text-gray-400">
                ({drafts.length} borrador{drafts.length !== 1 ? 'es' : ''})
              </span>
            </h2>
            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {drafts.map((draft) => (
                <DraftCard key={draft.id} draft={draft} />
              ))}
            </div>
          </section>
        )
      })}
    </div>
  )
}
