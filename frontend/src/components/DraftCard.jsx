const STATUS_STYLES = {
  PENDING_REVIEW: 'bg-yellow-100 text-yellow-800',
  EDITED: 'bg-blue-100 text-blue-800',
  APPROVED: 'bg-green-100 text-green-800',
  REJECTED: 'bg-red-100 text-red-800',
}

export function DraftCard({ draft }) {
  const statusClass = STATUS_STYLES[draft.status] ?? 'bg-gray-100 text-gray-700'

  return (
    <div className="rounded-xl border border-gray-200 bg-white p-4 shadow-sm flex flex-col gap-2">
      <div className="flex items-center justify-between">
        <span className="text-xs font-semibold text-gray-400 uppercase tracking-wide">
          #{draft.id}
        </span>
        <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${statusClass}`}>
          {draft.status.replace('_', ' ')}
        </span>
      </div>
      <p className="text-sm text-gray-700 leading-relaxed line-clamp-4">
        {draft.preview}
      </p>
      {draft.createdAt && (
        <span className="text-xs text-gray-400">
          {new Date(draft.createdAt).toLocaleString()}
        </span>
      )}
    </div>
  )
}
