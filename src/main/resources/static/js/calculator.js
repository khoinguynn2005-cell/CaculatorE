(() => {
    const display = document.querySelector('#display');
    const expression = document.querySelector('#expression');
    const error = document.querySelector('#error');
    const keypad = document.querySelector('.keypad');
    const operatorButtons = [...document.querySelectorAll('[data-operation]')];

    let current = '0';
    let stored = null;
    let operation = null;
    let waitingForOperand = false;
    let justCalculated = false;
    let busy = false;

    const symbols = { add: '+', subtract: '−', multiply: '×', divide: '÷' };

    function render() {
        display.textContent = current;
        operatorButtons.forEach(button => {
            button.classList.toggle('is-selected', button.dataset.operation === operation);
        });
    }

    function clearError() { error.textContent = ''; }

    function enterDigit(digit) {
        clearError();
        if (waitingForOperand || justCalculated) {
            current = digit;
            waitingForOperand = false;
            justCalculated = false;
        } else if (current === '0') {
            current = digit;
        } else {
            current += digit;
        }
        render();
    }

    function enterDecimal() {
        clearError();
        if (waitingForOperand || justCalculated) {
            current = '0.';
            waitingForOperand = false;
            justCalculated = false;
        } else if (!current.includes('.')) {
            current += '.';
        }
        render();
    }

    async function chooseOperation(nextOperation) {
        clearError();
        if (operation && !waitingForOperand && stored !== null) {
            const left = stored;
            const right = current;
            const previousOperation = operation;
            busy = true;
            try {
                current = await requestCalculation(left, right, previousOperation);
                stored = current;
                expression.textContent = `${current} ${symbols[nextOperation]}`;
                operation = nextOperation;
                waitingForOperand = true;
                justCalculated = false;
                render();
            } catch (exception) {
                error.textContent = exception.message;
            } finally {
                busy = false;
            }
        } else {
            stored = current;
            operation = nextOperation;
            justCalculated = false;
        }
        waitingForOperand = true;
        expression.textContent = `${stored} ${symbols[operation]}`;
        render();
    }

    async function requestCalculation(left, right, selectedOperation) {
        const response = await fetch('/api/calculator/calculate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ left, right, operation: selectedOperation })
        });
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || 'Không thể thực hiện phép tính.');
        return data.result;
    }

    async function calculate() {
        if (!operation || stored === null || waitingForOperand || busy) return;
        const left = stored;
        const right = current;
        const selectedOperation = operation;
        busy = true;
        clearError();
        try {
            current = await requestCalculation(left, right, selectedOperation);
            expression.textContent = `${left} ${symbols[selectedOperation]} ${right} =`;
            stored = null;
            operation = null;
            waitingForOperand = true;
            justCalculated = true;
            render();
        } catch (exception) {
            error.textContent = exception.message;
        } finally {
            busy = false;
        }
    }

    async function percent() {
        if (busy) return;
        busy = true;
        clearError();
        try {
            current = await requestCalculation(current, null, 'percent');
            render();
        } catch (exception) {
            error.textContent = exception.message;
        } finally {
            busy = false;
        }
    }

    function clear() {
        current = '0';
        stored = null;
        operation = null;
        waitingForOperand = false;
        justCalculated = false;
        expression.innerHTML = '&nbsp;';
        clearError();
        render();
    }

    function toggleSign() {
        if (current === '0') return;
        current = current.startsWith('-') ? current.slice(1) : `-${current}`;
        clearError();
        render();
    }

    function backspace() {
        if (waitingForOperand || justCalculated) return;
        current = current.length <= 1 || (current.length === 2 && current.startsWith('-'))
            ? '0'
            : current.slice(0, -1);
        clearError();
        render();
    }

    keypad.addEventListener('click', event => {
        const button = event.target.closest('button');
        if (!button || busy) return;
        if (button.dataset.digit !== undefined) enterDigit(button.dataset.digit);
        else if (button.dataset.operation) chooseOperation(button.dataset.operation);
        else if (button.dataset.action === 'decimal') enterDecimal();
        else if (button.dataset.action === 'clear') clear();
        else if (button.dataset.action === 'sign') toggleSign();
        else if (button.dataset.action === 'percent') percent();
        else if (button.dataset.action === 'equals') calculate();
    });

    document.addEventListener('keydown', event => {
        if (busy) return;
        if (/^[0-9]$/.test(event.key)) enterDigit(event.key);
        else if (event.key === '.' || event.key === ',') enterDecimal();
        else if (event.key === 'Enter' || event.key === '=') { event.preventDefault(); calculate(); }
        else if (event.key === 'Escape') clear();
        else if (event.key === 'Backspace') backspace();
        else if (event.key === '+') chooseOperation('add');
        else if (event.key === '-') chooseOperation('subtract');
        else if (event.key === '*') chooseOperation('multiply');
        else if (event.key === '/') { event.preventDefault(); chooseOperation('divide'); }
        else if (event.key === '%') percent();
    });

    render();
})();
