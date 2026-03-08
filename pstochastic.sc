(
SynthDef.new(name:\simple, ugenGraphFunc:{
    | out, freq = 440|
    var source = SinOsc.ar(freq) * Env.perc(0.1,0.3,curve:-8).ar(doneAction: Done.freeSelf);
    Out.ar(bus:out, channelsArray:source)
}, rates:nil, prependArgs:nil, variants:nil, metadata:nil).add;

r = p({
    var currentStateIndex = 0;
    var states = [440, 880];
    var stateCount = states.size;
    var transitionMatrix = [
        [0.1,0.9],
        [1,0]
    ];
    var indexArray = Array.series(stateCount,0,1);
    loop {
        currentStateIndex = indexArray.wchoose(transitionMatrix[currentStateIndex]);
        states[currentStateIndex].yield;
    };
});

p = Routine(
    {
        var delta = 1/4;
        var pitchStream = r.asStream;
        loop {
            Synth(\simple, [\freq, pitchStream.next]);
            delta.yield;
        }
    }
);

p.play;
)
