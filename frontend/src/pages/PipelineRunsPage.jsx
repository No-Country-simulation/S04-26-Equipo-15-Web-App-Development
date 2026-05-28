import { usePipelineRuns } from '../hooks/usePipelineRuns'

const STATUS_CONFIG = {
  SUCCESS: {
    label: 'Exitoso',
    dot: 'bg-green-500',
    badge: 'bg-green-100 text-green-800',
  },
  PARTIAL: {
    label: 'Parcial',
    dot: 'bg-yellow-400',
    badge: 'bg-yellow-100 text-yellow-800',
  },
  FAILED: {
    label: 'Fallido',
    dot: 'bg-red-500',
    badge: 'bg-red-100 text-red-800',
  },
}

function PipelineRunRow({ run }) {
  const config = STATUS_CONFIG[run.status] ?? {
    label: run.status,
    dot: 'bg-gray-400',
    badge: 'bg-gray-100 text-gray-700',
  }

  return (
    <tr className="border-t border-gray-100 hover:bg-gray-50 transition-colors">
      <td className="px-4 py-3 text-sm text-gray-500">#{run.id}</td>
      <td className="px-4 py-3">
        <span className={`inline-flex items-center gap-1.5 text-xs font-medium px-2.5 py-0.5 rounded-full ${config.badge}`}>
          <span className={`w-1.5 h-1.5 rounded-full ${config.dot}`} />
          {config.label}
        </span>
      </td>
      <td className="px-4 py-3 text-sm text-gray-600">
        {new Date(run.startedAt).toLocaleString()}
      </td>
      <td className="px-4 py-3 text-sm text-gray-600">
        {run.finishedAt ? new Date(run.finishedAt).toLocaleString() : '—'}
      </td>
      <td className="px-4 py-3 text-sm text-gray-500 text-center">
        {run.draftsGenerated}
      </td>
    </tr>
  )
}

export function PipelineRunsPage() {
  const { data, loading, error } = usePipelineRuns()

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
        <strong>Error al cargar ejecuciones:</strong> {error}
      </div>
    )
  }

  if (!data?.length) {
    return (
      <p className="text-center text-gray-400 py-16">
        No hay ejecuciones del pipeline registradas.
      </p>
    )
  }

  return (
    <div className="overflow-x-auto rounded-xl border border-gray-200 shadow-sm">
      <table className="w-full text-left">
        <thead className="bg-gray-50 text-xs text-gray-500 uppercase tracking-wide">
          <tr>
            <th className="px-4 py-3">ID</th>
            <th className="px-4 py-3">Estado</th>
            <th className="px-4 py-3">Inicio</th>
            <th className="px-4 py-3">Fin</th>
            <th className="px-4 py-3 text-center">Borradores</th>
          </tr>
        </thead>
        <tbody>
          {data.map((run) => (
            <PipelineRunRow key={run.id} run={run} />
          ))}
        </tbody>
      </table>
    </div>
  )
}
