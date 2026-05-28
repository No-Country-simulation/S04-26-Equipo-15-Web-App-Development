import { useState } from 'react'
import { DraftListPage } from './pages/DraftListPage'
import { PipelineRunsPage } from './pages/PipelineRunsPage'

const TABS = [
  { id: 'drafts', label: 'Borradores pendientes' },
  { id: 'pipeline', label: 'Historial de pipeline' },
]

export default function App() {
  const [activeTab, setActiveTab] = useState('drafts')

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 px-6 py-4">
        <h1 className="text-xl font-bold text-gray-800 tracking-tight">
          TalentCircle <span className="text-purple-600">Content Bot</span>
        </h1>
      </header>

      {/* Tabs */}
      <nav className="bg-white border-b border-gray-200 px-6">
        <div className="flex gap-1">
          {TABS.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`px-4 py-3 text-sm font-medium border-b-2 transition-colors ${
                activeTab === tab.id
                  ? 'border-purple-600 text-purple-700'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>
      </nav>

      {/* Content */}
      <main className="max-w-6xl mx-auto px-6 py-8">
        {activeTab === 'drafts' && <DraftListPage />}
        {activeTab === 'pipeline' && <PipelineRunsPage />}
      </main>
    </div>
  )
}
