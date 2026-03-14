// This is a simple example using a saw wave synth. Walking up a scale, then applying randomness
(
SynthDef.new(name:\simple, ugenGraphFunc:{
    | out, freq = 440, pan = 0, dur = 1, atk = 0.1, dec = -4, amp = 0.5, width = 0.1 |
    var source = 
        Saw.ar(freq / 2, amp) *
        Env.perc( atk, dur, curve:dec).ar(doneAction: Done.freeSelf);
    var filtered = BPF.ar(
        source,
        freq,
        XLine.kr(1,0.01,dur),
    );
    var panned = Pan2.ar(filtered, pan);
    Out.ar(bus:out, channelsArray:panned)
}, rates:nil, prependArgs:nil, variants:nil, metadata:nil).add;
)

(
Tdef(\melody,
    {
        var stochasticPattern = p({
            | in |
            var currentStateIndex = 0;
            var states = in[0];
            var stateCount = states.size;
            var transitionMatrix = in[1];
            var indexArray = Array.series(stateCount,0,1);
            loop {
                currentStateIndex = indexArray.wchoose(transitionMatrix[currentStateIndex]);
                states[currentStateIndex].yield;
            };
        });
        var deltas = [1/4,1/2,1,2];
        var deltaProbs = [
            [0.9,0,0,0.1],
            [0.5,0.2,0.3,0],
            [0.4,0.4,0.2,0],
            [0.5,0.5,0,0],
        ];
        var deltaStream = stochasticPattern.asStream;
        var pitchStream = stochasticPattern.asStream;
        var pitches = (Scale.major.degrees + 60).midicps;

        // Pitches will transition randomly from one state to the next
        var pMat1 = [
            [0.1,0.3,0.3,0,0.3,0,0],
            [0,0.1,0.9,0,0,0,0],
            [0.1,0,0.1,0.3,0.5,0,0],
            [0,0,0,0.1,0.9,0,0],
            [0.5,0,0,0,0.1,0.4,0],
            [0,0,0.4,0,0.2,0.1,0.3],
            [0.9,0,0,0,0,0.1,0],
        ];

        // Pitches are completely deterministic - ascending the scale
        var pMat2 = Array.fill2D(
            pitches.size,
            pitches.size,
            { | r, c | if ( c == (r + 1 % pitches.size) , 1,0) }
        );
        var nextPitch;
        loop {
            nextPitch = pitchStream.next([pitches,pMat1]);
            Synth(\simple, [
                \freq, nextPitch,
                \pan, 1.0.rand2,
                \dec, -8,
                \dur, 4,
                \atk, 0.01,
                \width, 0.01,
                \amp, 1
            ]);
            deltaStream.next([deltas, deltaProbs]).yield;
        }
    }
);
)

Tdef(\melody).play;
Tdef(\melody).stop;

