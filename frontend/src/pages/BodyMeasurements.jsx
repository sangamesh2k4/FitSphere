import { useCallback, useEffect, useState } from "react";

import { measurementService } from "../services/measurementService";

import MeasurementHeader from "../components/measurements/MeasurementHeader/MeasurementHeader";

import LatestMeasurements
    from "../components/measurements/LatestMeasurements/LatestMeasurements";

import MeasurementTrend
    from "../components/measurements/MeasurementTrend/MeasurementTrend";

import MeasurementHistory
    from "../components/measurements/MeasurementHistory/MeasurementHistory";

import MeasurementModalManager
    from "../components/measurements/MeasurementModalManager/MeasurementModalManager";

import "../styles/measurements/BodyMeasurements.css";


const metricOptions = [
    { value: "WEIGHT", label: "Weight", unit: "kg" },
    { value: "BODY_FAT", label: "Body Fat", unit: "%" },
    { value: "WAIST", label: "Waist", unit: "cm" },
    { value: "CHEST", label: "Chest", unit: "cm" },
    { value: "LEFT_ARM", label: "Left Arm", unit: "cm" },
    { value: "RIGHT_ARM", label: "Right Arm", unit: "cm" },
    { value: "LEFT_THIGH", label: "Left Thigh", unit: "cm" },
    { value: "RIGHT_THIGH", label: "Right Thigh", unit: "cm" }
];


function BodyMeasurements() {

    const [latest, setLatest] = useState(null);
    const [history, setHistory] = useState([]);
    const [trend, setTrend] = useState([]);
    const [historyPage, setHistoryPage] = useState(0);
    const [historyHasMore, setHistoryHasMore] = useState(true);
    const [selectedMetric, setSelectedMetric] =
        useState("WEIGHT");

    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [saving, setSaving] = useState(false);
    const [editingMeasurement, setEditingMeasurement] =
        useState(null);


    const loadMeasurements = async () => {

        try {

            setLoading(true);

            const [
                latestData,
                historyData
            ] = await Promise.all([
                measurementService.getLatestMeasurement(),
                measurementService.getMeasurementHistory(0)
            ]);

            setLatest(latestData);
            setHistory(historyData.content);
            setHistoryPage(historyData.number);
            setHistoryHasMore(!historyData.last);

        } catch (error) {

            console.error(
                "Failed to load measurements",
                error
            );

        } finally {

            setLoading(false);

        }
    };

    const loadMoreHistory = async () => {
    if (!historyHasMore) return;

    try {
        const nextPage = historyPage + 1;

        const data =
            await measurementService.getMeasurementHistory(nextPage);

        setHistory(previous => [
            ...previous,
            ...data.content
        ]);

        setHistoryPage(data.number);
        setHistoryHasMore(!data.last);

    } catch (error) {
        console.error(
            "Failed to load more measurement history",
            error
        );
    }
};

    const loadTrend = useCallback(async () => {

        try {

            const data =
                await measurementService
                    .getMeasurementTrend(
                        selectedMetric
                    );

            setTrend(data);

        } catch (error) {

            console.error(
                "Failed to load measurement trend",
                error
            );

        }

    }, [selectedMetric]);


    useEffect(() => {
        loadMeasurements();
    }, []);


    useEffect(() => {
        loadTrend();
    }, [loadTrend]);


    const handleSaveMeasurement = async (request) => {

        try {

            setSaving(true);

            if (editingMeasurement?.id) {

                await measurementService
                    .updateMeasurement(
                        editingMeasurement.id,
                        request
                    );

            } else {

                await measurementService
                    .addMeasurement(request);

            }

            setShowForm(false);
            setEditingMeasurement(null);

            await Promise.all([
                loadMeasurements(),
                loadTrend()
            ]);

        } catch (error) {

            console.error(
                "Failed to save measurement",
                error
            );

        } finally {

            setSaving(false);

        }
    };


    const handleEdit = (measurement) => {

        setEditingMeasurement(measurement);
        setShowForm(true);

    };


   const handleDelete = async (id) => {
    try {
        await measurementService.deleteMeasurement(id);

        await Promise.all([
            loadMeasurements(),
            loadTrend()
        ]);

    } catch (error) {
        console.error(
            "Failed to delete measurement",
            error
        );
    }
};


    if (loading && !latest && history.length === 0) {

        return (
            <div className="measurements-loading">
                Loading measurements...
            </div>
        );

    }


    return (

        <main className="measurements-page">

            <MeasurementHeader
                onLogMeasurement={() => {
                    setEditingMeasurement(null);
                    setShowForm(true);
                }}
            />


            <LatestMeasurements
                latest={latest}
            />


            <MeasurementTrend
                trend={trend}
                selectedMetric={selectedMetric}
                onMetricChange={setSelectedMetric}
                metricOptions={metricOptions}
            />


            <MeasurementHistory
                history={history}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onLoadMore={loadMoreHistory}
                hasMore={historyHasMore}
            />


<MeasurementModalManager
    open={showForm}
    measurement={editingMeasurement}
    onSubmit={handleSaveMeasurement}
    onClose={() => {
        setShowForm(false);
        setEditingMeasurement(null);
    }}
    loading={saving}
/>
        </main>
    );
}


export default BodyMeasurements;